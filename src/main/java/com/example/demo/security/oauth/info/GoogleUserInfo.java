package com.example.demo.security.oauth.info;

import com.example.demo.entity.enums.LoginState;
import com.example.demo.entity.enums.Provider;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;

@Component
public class GoogleUserInfo {

    private final UserRepository userRepository;
    private final String infoUrl;

    public GoogleUserInfo(UserRepository userRepository,
                          @Value("${spring.security.oauth2.client.provider.google.user-info-uri}") String infoUrl) {
        this.userRepository = userRepository;
        this.infoUrl = infoUrl;
    }

    public UserInfo getGoogleUserInfo(String accessToken) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(infoUrl, HttpMethod.GET,
                    new HttpEntity<>(headers), String.class);

            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            String name = jsonNode.get("name").asText();
            String email = jsonNode.get("email").asText();

            // 이미 있는 사용자인지 분석
            if (userRepository.findByUserId(email).isPresent()) {
                return new UserInfo(name, email, Provider.GOOGLE, LoginState.Login); // 이미 있는 회원
            } else {
                return new UserInfo(name, email, Provider.GOOGLE, LoginState.SignUP); // 새로운 회원
            }

        } catch (Exception e) {
            return new UserInfo("Error", "Failed to retrieve user info", null,  null);
        }
    }
}