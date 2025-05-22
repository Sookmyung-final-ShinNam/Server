package com.example.demo.extra.fairyTale.service;

import com.example.demo.base.api.ApiResponse;

public interface FairyTaleService {

    ApiResponse<?> getMyFairyTalesWithType(String userId, String type);
    ApiResponse<?> getMyFairyTalesWithFavorite(String userId);

    ApiResponse<?> getMyFairyTale(String userId, Long fairyTaleId);

    ApiResponse<?> updateFavoriteStatus(String userId, Long fairyTaleId);
}