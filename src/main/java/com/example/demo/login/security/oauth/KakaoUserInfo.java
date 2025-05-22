package com.example.demo.login.security.oauth;

import com.example.demo.domain.entity.enums.Provider;
import com.example.demo.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Component
public class KakaoUserInfo {

    private final UserRepository userRepository;
    private final String infoUrl;

    public KakaoUserInfo(UserRepository userRepository,
                         @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}") String infoUrl ) {
        this.userRepository = userRepository;
        this.infoUrl = infoUrl;
    }

    public UserInfo getKakaoUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(infoUrl, HttpMethod.GET,
                    new org.springframework.http.HttpEntity<>(headers), String.class);

            String responseBody = response.getBody();
            String nickname = responseBody.contains("nickname") ?
                    responseBody.split("nickname\":\"")[1].split("\"")[0] : "닉네임 없음";
            String email = responseBody.contains("email") ?
                    responseBody.split("email\":\"")[1].split("\"")[0] : "이메일 없음";

            // 이미 있는 사용자인지 분석
            if (userRepository.findByEmail(email).isPresent()) {
                return new UserInfo(nickname, email, Provider.KAKAO, "Login");
            } else {
                return new UserInfo(nickname, email, Provider.KAKAO, "SignUP");
            }

        } catch (Exception e) {
            return new UserInfo("Error", "Failed to retrieve user info", null, null);
        }
    }
}