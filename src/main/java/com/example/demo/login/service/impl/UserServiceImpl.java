package com.example.demo.login.service.impl;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.base.api.status.SuccessStatus;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.domain.entity.enums.Status;
import com.example.demo.domain.entity.User;
import com.example.demo.login.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // 1. 로그아웃 API
    @Override
    public ApiResponse<?> logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        user.deactivate(); // 비활성화 상태로 설정
        userRepository.save(user);

        return ApiResponse.of(SuccessStatus.USER_LOGOUT_SUCCESS, null);
    }

    // 2. 회원탈퇴 API
    @Override
    public ApiResponse<?> deleteUser(String email) {
        User user = userRepository.findByEmailAndActive(email, Status.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        user.withdraw(); // 탈퇴 상태로 설정 & 탈퇴 시간 기록 ( 탈퇴 시간 이후로 일정 시간 이상 지나면 모든 정보가 삭제됨 -> 스케줄링 적용 )
        userRepository.save(user);

        return ApiResponse.of(SuccessStatus.USER_WITHDRAWN_SUCCESS, user);
    }

}