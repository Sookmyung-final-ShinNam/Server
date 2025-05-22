package com.example.demo.login.service;

import com.example.demo.base.api.ApiResponse;

public interface UserService {

    // 1. 로그아웃 : 유저 아이디로 로그아웃 처리
    ApiResponse<?> logout(String userId);

    // 2. 회원 탈퇴 : 유저 아이디로 회원 탈퇴 처리
    ApiResponse<?> deleteUser(String userId);

}