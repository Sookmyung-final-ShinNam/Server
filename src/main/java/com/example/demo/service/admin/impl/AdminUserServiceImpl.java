package com.example.demo.service.admin.impl;

import com.example.demo.entity.base.User;
import com.example.demo.entity.enums.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.service.admin.AdminUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    public AdminUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse<?> turnUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        user.setRole(Role.USER);
        userRepository.save(user);
        return ApiResponse.of(SuccessStatus.ADMIN_TURN_USER, null);
    }

    @Override
    public ApiResponse<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ApiResponse.of(SuccessStatus.ADMIN_GET_ALL_USER, users);
    }

}