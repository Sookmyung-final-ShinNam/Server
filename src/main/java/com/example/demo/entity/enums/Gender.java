package com.example.demo.entity.enums;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;

public enum Gender {
    MALE, FEMALE;

    public static Gender fromString(String value) {
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new CustomException(ErrorStatus.FAIRY_CREATE_FAILED);
        }
    }
}