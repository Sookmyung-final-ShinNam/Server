package com.example.demo.service.general.impl;

import com.example.demo.entity.enums.Role;
import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.entity.base.User;
import com.example.demo.entity.enums.Status;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse<?> logout(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        user.deactivate();
        userRepository.save(user);
        return ApiResponse.of(SuccessStatus.USER_LOGOUT_SUCCESS, null);
    }

    @Override
    public ApiResponse<?> deleteUser(String userId) {
        User user = userRepository.findByUserIdAndActive(userId, Status.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        user.withdraw();
        userRepository.save(user);
        return ApiResponse.of(SuccessStatus.USER_WITHDRAWN_SUCCESS, user);
    }

    @Override
    public ApiResponse<?> turnAdmin(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        user.setRole(Role.ADMIN);
        userRepository.save(user);
        return ApiResponse.of(SuccessStatus.ADMIN_TURN_ADMIN, null);
    }

}