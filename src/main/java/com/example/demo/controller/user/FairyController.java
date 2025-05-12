package com.example.demo.controller.user;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.fairy.FairyInfoRequest;
import com.example.demo.domain.dto.fairy.FairyMixRequest;
import com.example.demo.service.FairyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fairy")
public class FairyController extends BaseController {

    @Autowired
    private FairyService fairyService;

    // 사용자 입력 받아 나의 요정 생성 (초기 단계)
    @PostMapping("/info")
    ApiResponse<?> createFairyInfo(@RequestBody FairyInfoRequest request) {
        String userId = getCurrentUserId();
        return fairyService.createFairyInfo(userId, request);
    }

    @PostMapping("/mix")
    public ApiResponse<?> createFairyMix(@RequestBody @Valid FairyMixRequest request) {
        String userId = getCurrentUserId();
        return fairyService.createFairyMix(userId, request);
    }

    // 나의 요정 조회
    @GetMapping("/{fairyId}")
    public ApiResponse<?> getMyFairy(@RequestParam Long fairyId) {
        String userId = getCurrentUserId();
        return fairyService.getMyFairy(userId, fairyId);
    }

    // 나의 요정 목록 조회
    @GetMapping
    public ApiResponse<?> getMyFairies(@RequestParam(defaultValue = "all") String gender) {
        String userId = getCurrentUserId();
        return fairyService.getMyFairies(userId, gender);
    }
}