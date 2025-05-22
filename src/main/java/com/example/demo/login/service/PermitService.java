package com.example.demo.login.service;

import com.example.demo.base.api.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public interface PermitService {

    ApiResponse<?> refreshToken(String refreshToken);

}