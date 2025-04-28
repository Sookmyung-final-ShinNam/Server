package com.example.demo.domain.converter;

import com.example.demo.entity.base.Token;
import com.example.demo.entity.base.User;
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