package com.example.demo.emotionInterface.controller;

import com.example.demo.base.BaseController;
import com.example.demo.base.api.ApiResponse;
import com.example.demo.emotionInterface.service.EmotionInterfaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class EmotionInterfaceController extends BaseController {

    private final EmotionInterfaceService emotionInterfaceService;

    // 1. text 에서 감정 인식 ( 웹뷰 )

    @GetMapping("/text-emotion-interface")
    public ApiResponse<String> emotionInterface(@RequestParam String text) {
        String userId = getCurrentUserId();
        return emotionInterfaceService.emotionHtml(userId, text);
    }


}