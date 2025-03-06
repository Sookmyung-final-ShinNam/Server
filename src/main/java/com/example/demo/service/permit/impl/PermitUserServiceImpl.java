package com.example.demo.service.permit.impl;

import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.base.ApiResponse;
import com.example.demo.domain.converter.TokenConverter;
import com.example.demo.domain.dto.UserRequest;
import com.example.demo.domain.dto.TokenResponse;
import com.example.demo.entity.base.Token;
import com.example.demo.entity.base.User;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtUtil;
import com.example.demo.domain.converter.UserConverter;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.service.permit.PermitUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PermitUserServiceImpl implements PermitUserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private TokenConverter tokenConverter;

    @Override
    public ResponseEntity<ApiResponse<TokenResponse>> signup(UserRequest userRequest) {
        ensureUserNotExists(userRequest.getUserId());
        ensureUserNameNotExists(userRequest.getName());
        User newUser = saveNewUser(userRequest);
        TokenResponse tokenResponse = generateAndSaveToken(newUser);
        return buildSuccessResponse(SuccessStatus.USER_SIGNUP_SUCCESS, tokenResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<TokenResponse>> login(String userId, String password) {
        User user = validateUserCredentials(userId, password);
        TokenResponse tokenResponse = reLogin(user.getUserId());
        return buildSuccessResponse(SuccessStatus.USER_LOGIN_SUCCESS, tokenResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(String refreshToken) {
        String userId = validateRefreshToken(refreshToken);
        TokenResponse tokenResponse = reLogin(userId);
        return buildSuccessResponse(SuccessStatus.TOKEN_REFRESH_SUCCESS, tokenResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<TokenResponse>> socialLogin(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        Token token = tokenRepository.findByUser(user).get();
        TokenResponse tokenResponse = tokenConverter.toResponse(token);
        return buildSuccessResponse(SuccessStatus.USER_LOGIN_SUCCESS, tokenResponse);
    }

    private void ensureUserNameNotExists(String name) {
        if (userRepository.findByUserName(name).isPresent()) {
            throw new CustomException(ErrorStatus.USER_ALREADY_EXISTS);
        }
    }

    private void ensureUserNotExists(String userId) {
        if (userRepository.findByUserId(userId).isPresent()) {
            throw new CustomException(ErrorStatus.USER_ALREADY_EXISTS);
        }
    }

    private User saveNewUser(UserRequest userRequest) {
        User newUser = userConverter.toEntity(userRequest);
        return userRepository.save(newUser);
    }

    private User validateUserCredentials(String userId, String password) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        if (!user.getPassword().equals(password)) {
            throw new CustomException(ErrorStatus.USER_INVALID_PASSWORD);
        }

        return user;
    }

    private String validateRefreshToken(String refreshToken) {
        if (!jwtUtil.isValidRefreshToken(refreshToken)) {
            throw new CustomException(ErrorStatus.TOKEN_INVALID_REFRESH_TOKEN);
        }

        String userId = jwtUtil.extractUserIdFromToken(refreshToken);
        if (userId == null) {
            throw new CustomException(ErrorStatus.TOKEN_INVALID_REFRESH_TOKEN);
        }

        return userId;
    }

    private TokenResponse generateAndSaveToken(User user) {
        Token token = createNewToken(user);
        return tokenConverter.toResponse(token);
    }

    private Token createNewToken(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        Token token = tokenConverter.toEntity(accessToken, refreshToken, user);
        token.setCreatedAt(LocalDateTime.now());
        token.setUpdatedAt(LocalDateTime.now());

        return tokenRepository.save(token);
    }

    private TokenResponse reLogin(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Token token = tokenRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorStatus.TOKEN_NOT_FOUND));

        return updateToken(token, userId);
    }

    private TokenResponse updateToken(Token token, String userId) {
        token.setAccessToken(jwtUtil.generateAccessToken(userId));
        token.setRefreshToken(jwtUtil.generateRefreshToken(userId));
        token.setUpdatedAt(LocalDateTime.now());

        tokenRepository.save(token);
        activateUser(token.getUser());

        return tokenConverter.toResponse(token);
    }

    private void activateUser(User user) {
        user.activate();
        userRepository.save(user);
    }

    private ResponseEntity<ApiResponse<TokenResponse>> buildSuccessResponse(SuccessStatus status, TokenResponse response) {
        return ResponseEntity.ok(ApiResponse.of(status, response));
    }

}