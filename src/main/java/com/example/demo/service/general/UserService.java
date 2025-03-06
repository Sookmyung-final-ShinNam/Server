package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;

public interface UserService {
    ApiResponse<?> logout(String userId);
    ApiResponse<?> deleteUser(String userId);
    ApiResponse<?> turnAdmin(String userId);
}