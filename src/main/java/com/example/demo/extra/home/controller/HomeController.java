package com.example.demo.extra.home.controller;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.BaseController;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.extra.home.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/main")
public class HomeController extends BaseController {

    @Autowired
    private HomeService mainService;

    // 메인 화면
    @GetMapping
    ApiResponse<?> getMain() {
        String userId = getCurrentUserId();
        return mainService.getMain(userId);
    }

    // 요정/동화 최대 갯수 늘리기
    @PatchMapping("/{type}")
    public ApiResponse<?> updateMain(@PathVariable String type) {
        String userId = getCurrentUserId();

        if (type.equalsIgnoreCase("fairy")) {
            return mainService.increaseMaxFairyCount(userId);
        } else if (type.equalsIgnoreCase("story")) {
            return mainService.increaseMaxStoryCount(userId);
        } else {
            throw new CustomException(ErrorStatus.INVALID_REQUEST); // 예외 처리
        }
    }

}