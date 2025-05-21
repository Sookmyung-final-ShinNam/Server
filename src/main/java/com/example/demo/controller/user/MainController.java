package com.example.demo.controller.user;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/main")
public class MainController extends BaseController {

    @Autowired
    private MainService mainService;

    // 메인 화면
    @GetMapping
    ApiResponse<?> getMain() {
        String userId = getCurrentUserId();
        return mainService.getMain(userId);
    }
}
