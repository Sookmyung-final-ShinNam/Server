package com.example.demo.service.permit;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.TokenResponse;
import com.example.demo.domain.dto.UserRequest;
import org.springframework.http.ResponseEntity;

public interface PermitUserService {
    ResponseEntity<ApiResponse<TokenResponse>> signup(UserRequest userRequest);
    ResponseEntity<ApiResponse<TokenResponse>> login(String userId, String password);
    ResponseEntity<ApiResponse<TokenResponse>> refreshToken(String refreshToken);
    ResponseEntity<ApiResponse<TokenResponse>> socialLogin(String userName);
}