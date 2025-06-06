package com.example.demo.payment.controller;

import com.example.demo.base.BaseController;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.payment.dto.ApproveResponse;
import com.example.demo.payment.dto.PaymentConverter;
import com.example.demo.payment.dto.PaymentRequest;
import com.example.demo.payment.dto.ReadyResponse;
import com.example.demo.domain.repository.PaymentRepository;
import com.example.demo.payment.service.PaymentService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController extends BaseController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${kakao.redirect-url}")
    private String redirectUrl;

    @Value("${kakao.domain}")
    private String domain;

    private final UserRepository userRepository;


    @GetMapping("/permit/payment/approve")
    public String approvePayment(@RequestParam String pg_token, HttpServletRequest request) {

        // 쿠키에서 TID 값을 찾기
        String tid = findTidFromCookies(request);

        logger.info("pg_token: {}", pg_token);
        logger.info("tid: {}", tid);

        // 결제 승인 처리
        ApproveResponse approveResponse = paymentService.approvePayment(pg_token, tid).getResult();

        // 결제 금액 가져오기
        int amount = paymentRepository.findByTid(tid).get().getAmount();

        // 리다이렉트 URL 동적 생성
        String redirectTarget = redirectUrl + "/?approvedAt=" + approveResponse.getApprovedAt().toString()
                + "&approvedAmount=" + amount;


        // 결제 성공시 포인트 증가

        String userId = paymentRepository.findByTid(tid).get().getUserId();

        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        user.setPoint(user.getPoint() + amount);
        userRepository.save(user);


        return "redirect:" + redirectTarget;
    }

    @GetMapping("/permit/payment/redirect")
    public String redirectPayment(@RequestParam String tid, @RequestParam String url, HttpServletResponse response) {

        // tid를 쿠키로 저장
        Cookie cookie = new Cookie("tid", tid);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);

        // application.yml에서 설정된 도메인 사용
        cookie.setDomain(domain);

        response.addCookie(cookie);

        logger.info("Saved TID in cookie: {}", tid);
        return "redirect:" + url;
    }

    @PostMapping("/payment/create")
    public ResponseEntity<Map<String, String>> createPayment(@RequestParam String itemId, @RequestParam String itemName, @RequestParam int totalAmount) {

        String userId = getCurrentUserId();  // 현재 로그인 된 사용자 ID

        PaymentRequest paymentRequest = PaymentConverter.toPaymentRequest(userId, itemId, itemName, totalAmount);

        ReadyResponse readyResponse = paymentService.preparePayment(paymentRequest).getResult();

        String tid = readyResponse.getTid();
        String url = readyResponse.getNextRedirectMobileUrl();

        logger.info("KakaoPay ReadyResponse - TID: {}, Redirect URL: {}", tid, url);

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/api/permit/payment/redirect?tid=" + tid + "&url=" + url);

        return ResponseEntity.ok(response);
    }


    private String findTidFromCookies(HttpServletRequest request) {
        String tid = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("tid".equals(cookie.getName())) {
                    tid = cookie.getValue();
                    break;
                }
            }
        }
        return tid;
    }

}