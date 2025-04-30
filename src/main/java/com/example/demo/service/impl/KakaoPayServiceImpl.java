package com.example.demo.service.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.payment.KakaoPayConverter;
import com.example.demo.domain.dto.payment.ApproveResponse;
import com.example.demo.domain.dto.payment.PaymentRequest;
import com.example.demo.domain.dto.payment.ReadyResponse;
import com.example.demo.entity.base.payment.Payment;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class KakaoPayServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(KakaoPayServiceImpl.class);

    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;
    private final KakaoPayConverter kakaoPayConverter;
    private final String secretKey;

    public KakaoPayServiceImpl(
            @Value("${kakao.pay.cid}") String cid,
            @Value("${kakao.pay.secretKey}") String secretKey,
            @Value("${kakao.pay.approvalUrl}") String approvalUrl,
            @Value("${kakao.pay.cancelUrl}") String cancelUrl,
            @Value("${kakao.pay.failUrl}") String failUrl,
            PaymentRepository paymentRepository
    ) {
        this.secretKey = secretKey;
        this.paymentRepository = paymentRepository;
        this.restTemplate = new RestTemplate();
        this.kakaoPayConverter = new KakaoPayConverter(cid, approvalUrl, cancelUrl, failUrl);
    }


    // 결제 준비 요청
    @Override
    public ApiResponse<ReadyResponse> preparePayment(PaymentRequest paymentRequest) {

        // 결제 준비 파라미터 생성
        Map<String, Object> parameters = kakaoPayConverter.toPrepareParameters(paymentRequest);

        // KakaoPay 결제 준비 API 호출
        ReadyResponse readyResponse = sendRequest(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                parameters,
                ReadyResponse.class
        );

        // API 응답 데이터를 기반으로 Payment 엔티티 저장
        Optional.ofNullable(readyResponse).ifPresent(response -> {
            Payment paymentEntity = kakaoPayConverter.toEntity(paymentRequest, response);
            savePaymentEntity(paymentEntity);
        });

        return ApiResponse.of(SuccessStatus.PAYMENT_READY_SUCCESS, readyResponse);
    }

    // 결제 승인 요청
    @Override
    public ApiResponse<ApproveResponse> approvePayment(String pgToken, String tid) {

        // TID로 결제 정보 조회
        Payment payment = findPaymentByTid(tid);

        // 결제 승인 API 호출
        ApproveResponse approveResponse = sendRequest(
                "https://open-api.kakaopay.com/online/v1/payment/approve",
                kakaoPayConverter.toApproveParameters(payment, pgToken),
                ApproveResponse.class
        );

        // 승인 성공 시 결제 상태 업데이트
        Optional.ofNullable(approveResponse).ifPresent(response -> {
            payment.setStatus("APPROVED");
            payment.setApprovedAt(LocalDateTime.now());
            savePaymentEntity(payment);
        });

        return ApiResponse.of(SuccessStatus.PAYMENT_APPROVE_SUCCESS, approveResponse);
    }


    // TID로 결제 정보 조회
    private Payment findPaymentByTid(String tid) {
        return paymentRepository.findByTid(tid)
                .orElseThrow(() -> new CustomException(ErrorStatus.PAYMENT_NOT_FOUND));
    }

    // Payment 엔티티 저장
    private void savePaymentEntity(Payment paymentEntity) {
        try {
            paymentRepository.save(paymentEntity);
        } catch (Exception e) {
            logger.error("결제 정보 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.PAYMENT_SAVE_FAILED);
        }
    }

    // HTTP 요청을 위한 헤더 생성
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // HTTP 요청 전송
    private <T> T sendRequest(String url, Map<String, Object> parameters, Class<T> responseType) {
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, createHeaders());
            ResponseEntity<T> response = restTemplate.postForEntity(url, requestEntity, responseType);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                logger.error("API 요청 실패: 상태 코드 - {}", response.getStatusCode());
                throw new CustomException(ErrorStatus.PAYMENT_REQUEST_FAILED);
            }
        } catch (Exception e) {
            logger.error("API 요청 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.PAYMENT_REQUEST_ERROR);
        }
    }

}