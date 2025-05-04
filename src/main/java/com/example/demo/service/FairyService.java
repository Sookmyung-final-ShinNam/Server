package com.example.demo.service;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.fairy.FairyRequest;

public interface FairyService {

    ApiResponse<?> createFairy(String userId, FairyRequest request);

    ApiResponse<?> getMyFairies(String userId);

}