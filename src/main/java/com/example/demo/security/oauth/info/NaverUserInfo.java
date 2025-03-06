package com.example.demo.security.oauth.info;

import com.example.demo.entity.enums.LoginState;
import com.example.demo.entity.enums.Provider;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Component
public class NaverUserInfo {

    private final UserRepository userRepository;
    private final String infoUrl;

    public NaverUserInfo(UserRepository userRepository,
                         @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}") String infoUrl ) {
        this.userRepository = userRepository;
        this.infoUrl = infoUrl;
    }

    public UserInfo getNaverUserInfo(String accessToken) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(infoUrl, HttpMethod.GET, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            JsonNode responseNode = jsonNode.path("response");
            String name = responseNode.path("name").asText();
            String email = responseNode.path("email").asText();

            // 이미 있는 사용자인지 분석
            if (userRepository.findByUserId(email).isPresent()) {
                return new UserInfo(name, email, Provider.NAVER, LoginState.Login); // 이미 있는 회원
            } else {
                return new UserInfo(name, email, Provider.NAVER, LoginState.SignUP); // 새로운 회원
            }

        } catch (Exception e) {
            return new UserInfo("Error", "Failed to retrieve user info", null, null);
        }
    }
}