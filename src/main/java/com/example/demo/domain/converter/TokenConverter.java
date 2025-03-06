package com.example.demo.domain.converter;

import com.example.demo.domain.dto.TokenResponse;
import com.example.demo.entity.base.Token;
import com.example.demo.entity.base.User;
import org.springframework.stereotype.Component;

@Component
public class TokenConverter {

    public Token toEntity(String token, String refreshToken, User user) {
        return Token.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .user(user)
                .build();
    }

    public TokenResponse toResponse(Token token) {
        return new TokenResponse(token.getAccessToken(), token.getRefreshToken(), token.getCreatedAt(), token.getUpdatedAt());
    }

}