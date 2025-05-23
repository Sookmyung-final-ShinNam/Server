package com.example.demo.mix.controller;

import com.example.demo.base.BaseController;
import com.example.demo.base.api.ApiResponse;
import com.example.demo.mix.dto.MixFairyTaleRequest;
import com.example.demo.mix.service.MixService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class MixController extends BaseController {

    private final MixService chatService;

    // 1. 캐릭터 기반 새로운 동화 생성
    @PostMapping("/mix-fiaryTale")
    public ApiResponse mixFiaryTale(@RequestBody MixFairyTaleRequest request) {
        String userId = getCurrentUserId();
        return chatService.mixFairyTale(userId, request);
    }


}