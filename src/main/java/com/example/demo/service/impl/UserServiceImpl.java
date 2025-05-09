package com.example.demo.service.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.entity.base.User;
import com.example.demo.entity.enums.Status;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // 1. 로그아웃 API
    @Override
    public ApiResponse<?> logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        user.deactivate(); // 비활성화 상태로 설정
        userRepository.save(user);

        return ApiResponse.of(SuccessStatus.USER_LOGOUT_SUCCESS, null);
    }

    // 2. 회원탈퇴 API
    @Override
    public ApiResponse<?> deleteUser(String username) {
        User user = userRepository.findByUsernameAndActive(username, Status.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        user.withdraw(); // 탈퇴 상태로 설정 & 탈퇴 시간 기록 ( 탈퇴 시간 이후로 일정 시간 이상 지나면 모든 정보가 삭제됨 -> 스케줄링 적용 )
        userRepository.save(user);

        return ApiResponse.of(SuccessStatus.USER_WITHDRAWN_SUCCESS, user);
    }

}