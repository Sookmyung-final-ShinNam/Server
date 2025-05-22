package com.example.demo.login.controller;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.BaseController;
import com.example.demo.login.service.PermitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permit")
public class PermitController extends BaseController {

    @Autowired
    private PermitService permitService;

    // 1. 리프레쉬 토큰 -> 액세스 토큰 반환
    @GetMapping("/token/refresh")
    public ApiResponse<?> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return permitService.refreshToken(refreshToken);
    }

}