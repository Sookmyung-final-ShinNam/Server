package com.example.demo.controller.user;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.service.FairyTaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.domain.dto.fairyTale.FairyTaleUpdateRequest;

@RestController
@RequestMapping("/api/fairy-tale")
public class FairyTaleController extends BaseController {

    @Autowired
    private FairyTaleService fairyTaleService;

    // 나의 동화 목록 조회
    @GetMapping
    public ApiResponse<?> getMyFairyTales() {
        String userId = getCurrentUserId();
        return fairyTaleService.getMyFairyTales(userId);
    }

    // 특정 동화 내용 업데이트
    @PutMapping("/{fairyTaleId}")
    public ApiResponse<?> updateFairyTaleContent(
            @PathVariable Long fairyTaleId,
            @RequestBody FairyTaleUpdateRequest request
    ) {
        String userId = getCurrentUserId();
        return fairyTaleService.updateFairyTaleContent(userId, fairyTaleId, request.getContent());
    }

    // 특정 동화 내용 조회
    @GetMapping("/{fairyTaleId}")
    public ApiResponse<?> getFairyTaleContent(@PathVariable Long fairyTaleId) {
        String userId = getCurrentUserId();
        return fairyTaleService.getFairyTaleContent(userId, fairyTaleId);
    }

}