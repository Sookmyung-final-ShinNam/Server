package com.example.demo.service.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.entity.base.Token;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.PermitService;
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