package com.example.demo.controller.permit;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.LoginRequest;
import com.example.demo.domain.dto.TokenResponse;
import com.example.demo.domain.dto.UserRequest;
import com.example.demo.service.permit.PermitUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permit/user")
public class PermitUserController {

    @Autowired
    private PermitUserService permituserService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(@Validated @RequestBody UserRequest userRequest) {
        return permituserService.signup(userRequest);
    }

    @PatchMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@RequestHeader String refreshToken) {
        return permituserService.refreshToken(refreshToken);
    }

    @PatchMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Validated @RequestBody LoginRequest loginRequest) {
        return permituserService.login(loginRequest.getUserId(), loginRequest.getPassword());
    }

    @GetMapping("/social-login")
    public ResponseEntity<ApiResponse<TokenResponse>> socialLogin(@RequestHeader String userName) {
        return permituserService.socialLogin(userName);
    }

}