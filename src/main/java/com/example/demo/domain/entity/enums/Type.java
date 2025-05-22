package com.example.demo.domain.entity.enums;

import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;

public enum Type {
    ONE, MORE;

    public static Type fromString(String value) {
        try {
            return Type.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new CustomException(ErrorStatus.FAIRY_TALE_INVALID_TYPE);
        }
    }
}