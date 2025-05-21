package com.example.demo.service;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.fairy.FairyInfoRequest;
import com.example.demo.domain.dto.fairy.FairyMixRequest;
import com.example.demo.domain.dto.fairy.FairyRequest;

public interface FairyService {

    ApiResponse<?> createFairyInfo(String userId, FairyInfoRequest request);
    ApiResponse<?> createFairy(String userId, FairyRequest request);
    ApiResponse<?> createFairyMix(String userId, FairyMixRequest request);

    ApiResponse<?> getMyFairy(String userId, Long fairyId);
    ApiResponse<?> getMyFairiesWithGender(String userId, String gender);
    ApiResponse<?> getMyFairiesWithFavorite(String userId);

    ApiResponse<?> updateFavoriteStatus(String userId, Long fairyId);
}