package com.example.demo.controller.user;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.fairyTale.FairyTaleRequest;
import com.example.demo.service.FairyTaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fairy-tale")
public class FairyTaleController extends BaseController {

    @Autowired
    private FairyTaleService fairyTaleService;

    // 동화 생성
    @PostMapping
    public ApiResponse<?> createFairyTale(@RequestBody FairyTaleRequest request) {
        String userId = getCurrentUserId();
        return fairyTaleService.createFairyTale(userId, request);
    }

    // 나의 동화 목록 조회
    @GetMapping
    public ApiResponse<?> getMyFairyTales() {
        String userId = getCurrentUserId();
        return fairyTaleService.getMyFairyTales(userId);
    }

}