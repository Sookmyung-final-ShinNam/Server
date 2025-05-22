package com.example.demo.service;

import com.example.demo.base.ApiResponse;

public interface FairyTaleService {

    ApiResponse<?> getMyFairyTalesWithType(String userId, String type);
    ApiResponse<?> getMyFairyTalesWithFavorite(String userId);

    ApiResponse<?> getMyFairyTale(String userId, Long fairyTaleId);

}