package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.service.general.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @PatchMapping("/logout")
    public ApiResponse<?> logout() {
        String userId = getCurrentUserId();
        return userService.logout(userId);
    }

    @DeleteMapping("/delete")
    public ApiResponse<?> deleteUser() {
        String userId = getCurrentUserId();
        return userService.deleteUser(userId);
    }

    @PatchMapping("/turn-admin")
    public ApiResponse<?> turnAdmin() {
        String userId = getCurrentUserId();
        return userService.turnAdmin(userId);
    }

}