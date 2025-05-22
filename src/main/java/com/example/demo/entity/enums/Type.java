package com.example.demo.entity.enums;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;

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