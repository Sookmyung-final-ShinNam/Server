package com.example.demo.controller.user;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
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
        return fairyTaleService.getFairyTaleContent(userId, fairyTaleId);
    }

}