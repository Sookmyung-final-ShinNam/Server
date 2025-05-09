package com.example.demo.entity.enums;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;

public enum Gender {
    Male, Female;

    public static Gender fromString(String value) {
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (Exception e) {
            // TODO: 잘못된 값 예외 처리 수정할
            throw new CustomException(ErrorStatus.FAIRY_CREATE_FAILED);
        }
    }
}