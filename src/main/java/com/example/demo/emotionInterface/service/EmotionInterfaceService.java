package com.example.demo.emotionInterface.service;

import com.example.demo.base.api.ApiResponse;

public interface EmotionInterfaceService {

    ApiResponse emotionHtml(String userId, String text);


}