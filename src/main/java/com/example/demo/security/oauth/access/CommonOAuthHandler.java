package com.example.demo.security.oauth.access;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.TokenConverter;
import com.example.demo.entity.base.Token;
import com.example.demo.entity.base.User;
import com.example.demo.entity.enums.Status;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtUtil;
import com.example.demo.security.oauth.info.KakaoUserInfo;
import com.example.demo.security.oauth.info.UserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class CommonOAuthHandler extends OncePerRequestFilter {

    private final String baseUrl;
    private final String redirectBaseUrl;
    private final String kakaoClientId;
    private final String kakaoClientSecret;
    private final String kakaoTokenUri;
    private final String kakaoRedirectUri;
    private String provider;

    @Autowired
    private KakaoUserInfo kakaoUserInfo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenConverter tokenConverter;

    public CommonOAuthHandler(
            @Value("${base-url}") String baseUrl,
            @Value("${redirect-url}") String redirectBaseUrl,
            @Value("${spring.security.oauth2.client.registration.kakao.client-id}") String kakaoClientId,
            @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") String kakaoClientSecret,
            @Value("${spring.security.oauth2.client.provider.kakao.token-uri}") String kakaoTokenUri,
            @Value("${spring.security.oauth2.client.kakao-path-uri}") String kakaoRedirectUri
    ) {
        this.baseUrl = baseUrl;
        this.redirectBaseUrl = redirectBaseUrl;
        this.kakaoClientId = kakaoClientId;
        this.kakaoClientSecret = kakaoClientSecret;
        this.kakaoTokenUri = kakaoTokenUri;
        this.kakaoRedirectUri = kakaoRedirectUri;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String tokenUri = null;

        if (path.equals(kakaoRedirectUri)) {
            tokenUri = kakaoTokenUri;
            provider = "kakao";
        }

        if (tokenUri != null) {
            String code = request.getParameter("code");
            if (code == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Authorization code is missing.");
                return;
            }

            MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
            tokenRequest.add("code", code);
            tokenRequest.add("client_id", getClientId(path));
            tokenRequest.add("client_secret", getClientSecret(path));
            tokenRequest.add("redirect_uri", baseUrl + path);
            tokenRequest.add("grant_type", "authorization_code");

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(tokenRequest, headers);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> tokenResponse = restTemplate.exchange(
                        tokenUri,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(tokenResponse.getBody());
                String accessToken = jsonNode.get("access_token").asText();

                UserInfo user = null;
                if (provider.equals("kakao")) {
                    user = kakaoUserInfo.getKakaoUserInfo(accessToken);
                }

                // 사용자 이메일로 회원가입 또는 로그인 처리
                String returnToken = "";
                if (userRepository.findByUserId(user.getEmail()).isPresent()) {

                    // 재로그인 ( 활성화 상태로 설정하고 새로운 토큰 생성, 만일 탈퇴 상태였다면 탈퇴 시간 삭제 )
                    Token token = tokenRepository.findByUser(userRepository.findByUserId(user.getEmail()).get())
                            .orElseThrow(() -> new CustomException(ErrorStatus.TOKEN_NOT_FOUND));
                    token.setAccessToken(jwtUtil.generateAccessToken(user.getEmail()));
                    token.setRefreshToken(jwtUtil.generateRefreshToken(user.getEmail()));
                    token.setUpdatedAt(LocalDateTime.now());

                    tokenRepository.save(token); // 업데이트 된 토큰 저장

                    token.getUser().activate(); // 활성화 상태로 설정
                    userRepository.save(token.getUser());

                    // 리프레쉬 토큰 반환
                    returnToken = token.getRefreshToken();

                } else {
                    // 회원가입
                    String userName = user.getName();

                    // 유저 객체 생성
                    User newUser = User.builder()
                            .userId(user.getEmail())
                            .userName(userName)
                            .provider(user.getProvider())
                            .active(Status.ACTIVE)  // 기본적으로 ACTIVE로 설정
                            .build();

                    userRepository.save(newUser); // 유저 DB에 저장

                    accessToken = jwtUtil.generateAccessToken(user.getEmail());
                    String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

                    Token token = tokenConverter.toEntity(accessToken, refreshToken, newUser);
                    token.setCreatedAt(LocalDateTime.now());
                    token.setUpdatedAt(LocalDateTime.now());

                    tokenRepository.save(token); // 토큰 DB에 저장

                    returnToken = token.getRefreshToken();
                }

                // 프론트로 리다이렉트 -> 리프레쉬 토큰 포함
                String redirectUrl = String.format("%s?token=%s", redirectBaseUrl, returnToken);
                response.setStatus(HttpServletResponse.SC_FOUND);
                response.setHeader("Location", redirectUrl);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to retrieve access token: " + e.getMessage());
            }
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientId(String path) {
        if (path.contains("kakao")) {
            return kakaoClientId;
        }
        return "";
    }

    private String getClientSecret(String path) {
        if (path.contains("kakao")) {
            return kakaoClientSecret;
        }
        return "";
    }
}