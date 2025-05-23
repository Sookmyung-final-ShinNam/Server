package com.example.demo.extra.fairy.controller;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.BaseController;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.extra.fairy.service.FairyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fairy")
public class FairyController extends BaseController {

    @Autowired
    private FairyService fairyService;


    // 나의 요정 목록 조회
    @GetMapping
    public ApiResponse<?> getMyFairies( @RequestParam(defaultValue = "all") String gender,
                                        @RequestParam(required = false) Boolean favorite) {
        String userId = getCurrentUserId();

        if (favorite != null && favorite) {
            if (!gender.equalsIgnoreCase("all")) {
                throw new CustomException(ErrorStatus.FAIRY_INVALID_FAVORITE);
            }
            return fairyService.getMyFairiesWithFavorite(userId);
        } else {
            return fairyService.getMyFairiesWithGender(userId, gender);
        }
    }

    // 나의 요정 조회
    @GetMapping("/{fairyId}")
    public ApiResponse<?> getMyFairy(@PathVariable Long fairyId) {
        String userId = getCurrentUserId();
        return fairyService.getMyFairy(userId, fairyId);
    }

    // 즐겨찾기 on/off
    @PatchMapping("/{fairyId}")
    public ApiResponse<?> updateFavoriteStatus(@PathVariable Long fairyId) {
        String userId = getCurrentUserId();
        return fairyService.updateFavoriteStatus(userId, fairyId);
    }

}