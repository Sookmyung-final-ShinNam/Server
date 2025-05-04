package com.example.demo.service;

import com.example.demo.base.ApiResponse;

public interface FairyTaleService {

    ApiResponse<?> getMyFairyTales(String userId);

    ApiResponse<?> updateFairyTaleContent(String userId, Long fairyTaleId, String content);

    ApiResponse<?> getFairyTaleContent(String userId, Long fairyTaleId);

}