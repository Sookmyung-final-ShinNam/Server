package com.example.demo.service;

import com.example.demo.base.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public interface PermitService {

    ApiResponse<?> refreshToken(String refreshToken);

}