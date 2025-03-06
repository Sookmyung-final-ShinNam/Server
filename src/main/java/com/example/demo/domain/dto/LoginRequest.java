package com.example.demo.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    @Size(min = 1, max = 50, message = "사용자 ID는 1자에서 50자 사이여야 합니다.")
    private String userId;

    @NotNull(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야 합니다.")
    private String password;

}