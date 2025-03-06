package com.example.demo.domain.converter;

import com.example.demo.entity.base.TempSocialLogin;
import com.example.demo.security.oauth.info.UserInfo;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class TempSocialLoginConverter {

    public TempSocialLogin toTempToken(UserInfo userInfo) {
        return TempSocialLogin.builder()
                .secretKey(UUID.randomUUID().toString()) // 시크릿 키 생성
                .userName("ShinNam") // 초기 이름 설정
                .userId(userInfo.getEmail())
                .password("myfirstpassword!") // 초기 비밀번호 설정
                .provider(userInfo.getProvider())
                .status(userInfo.getState())
                .build();
    }
}