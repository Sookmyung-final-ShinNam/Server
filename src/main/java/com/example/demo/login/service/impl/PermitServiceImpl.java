package com.example.demo.login.service.impl;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.base.api.status.SuccessStatus;
import com.example.demo.domain.entity.Token;
import com.example.demo.domain.repository.TokenRepository;
import com.example.demo.login.service.PermitService;
import org.springframework.stereotype.Service;

@Service
public class PermitServiceImpl implements PermitService {

    private final TokenRepository tokenRepository;

    public PermitServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public ApiResponse<?> refreshToken(String refreshToken) {
        Token token = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        return ApiResponse.of(SuccessStatus.USER_LOGIN_SUCCESS, token);
    }
}