package com.example.demo.domain.entity.enums;

import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;

public enum Gender {
    MALE, FEMALE;

    public static Gender fromString(String value) {
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new CustomException(ErrorStatus.FAIRY_INVALID_GENDER);
        }
    }
}