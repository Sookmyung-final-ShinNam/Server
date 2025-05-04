package com.example.demo.controller.user;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.service.FairyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fairy")
public class FairyController extends BaseController {

    @Autowired
    private FairyService fairyService;

    // 나의 요정 목록 조회
    @GetMapping
    public ApiResponse<?> getMyFairies() {
        String userId = getCurrentUserId();
        return fairyService.getMyFairies(userId);
    }

}