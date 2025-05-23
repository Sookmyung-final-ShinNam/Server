package com.example.demo.extra.fairyTale.controller;

import com.example.demo.base.BaseController;
import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.extra.fairyTale.service.FairyTaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fairy-tale")
@RequiredArgsConstructor
public class FairyTaleController extends BaseController {

    private final FairyTaleService fairyTaleService;

    // 나의 동화 목록 조회
    @GetMapping
    public ApiResponse<?> getMyFairyTales(@RequestParam(defaultValue = "all") String type,
                                          @RequestParam(required = false) Boolean favorite) {
        String userId = getCurrentUserId();

        if (favorite != null && favorite) {
            if (!type.equalsIgnoreCase("all")) {
                throw new CustomException(ErrorStatus.FAIRY_TALE_INVALID_FAVORITE);
            }
            return fairyTaleService.getMyFairyTalesWithFavorite(userId);
        } else {
            return fairyTaleService.getMyFairyTalesWithType(userId, type);
        }
    }

    // 특정 동화 내용 조회
    @GetMapping("/{fairyTaleId}")
    public ApiResponse<?> getFairyTaleContent(@PathVariable Long fairyTaleId) {
        String userId = getCurrentUserId();
        return fairyTaleService.getMyFairyTale(userId, fairyTaleId);
    }

    // 즐겨찾기 on/off
    @PatchMapping("/{fairyId}")
    public ApiResponse<?> updateFavoriteStatus(@PathVariable Long fairyId) {
        String userId = getCurrentUserId();
        return fairyTaleService.updateFavoriteStatus(userId, fairyId);
    }
}