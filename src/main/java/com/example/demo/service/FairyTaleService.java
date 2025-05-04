package com.example.demo.service;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.fairyTale.FairyTaleRequest;

public interface FairyTaleService {

    ApiResponse<?> createFairyTale(String userId, FairyTaleRequest request);

    ApiResponse<?> getMyFairyTales(String userId);

}