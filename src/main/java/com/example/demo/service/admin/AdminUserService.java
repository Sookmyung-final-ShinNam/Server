package com.example.demo.service.admin;

import com.example.demo.base.ApiResponse;

public interface AdminUserService {
    ApiResponse<?> turnUser(String userId);
    ApiResponse<?> getAllUsers();
}