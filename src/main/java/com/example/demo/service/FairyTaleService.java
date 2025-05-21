package com.example.demo.service;

import com.example.demo.base.ApiResponse;

public interface FairyTaleService {

    ApiResponse<?> getMyFairyTales(String userId);

    ApiResponse<?> getFairyTaleContent(String userId, Long fairyTaleId);

}