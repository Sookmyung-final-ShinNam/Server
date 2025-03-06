package com.example.demo.controller.admin;

import com.example.demo.controller.BaseController;
import com.example.demo.base.ApiResponse;
import com.example.demo.service.admin.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController extends BaseController {

    @Autowired
    private AdminUserService adminUserService;

    @PatchMapping("/turn-user")
    public ApiResponse<?> turnUser() {
        String userId = getCurrentUserId();
        return adminUserService.turnUser(userId);
    }

    @GetMapping("/get/all-user")
    public ApiResponse<?> getAllUsers() {
        return adminUserService.getAllUsers();
    }

}