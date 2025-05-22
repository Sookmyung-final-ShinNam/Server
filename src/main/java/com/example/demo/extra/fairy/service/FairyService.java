package com.example.demo.extra.fairy.service;

import com.example.demo.base.api.ApiResponse;

public interface FairyService {


    ApiResponse<?> getMyFairy(String userId, Long fairyId);
    ApiResponse<?> getMyFairiesWithGender(String userId, String gender);
    ApiResponse<?> getMyFairiesWithFavorite(String userId);

    ApiResponse<?> updateFavoriteStatus(String userId, Long fairyId);
}