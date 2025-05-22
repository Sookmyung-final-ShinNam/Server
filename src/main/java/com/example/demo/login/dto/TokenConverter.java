package com.example.demo.login.dto;

import com.example.demo.domain.entity.Token;
import com.example.demo.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TokenConverter {

    public Token toEntity(String token, String refreshToken, User user) {
        return Token.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .user(user)  // 사용자와의 1대 1관계 설정
                .build();
    }

}