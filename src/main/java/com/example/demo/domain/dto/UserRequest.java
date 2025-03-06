package com.example.demo.domain.dto;

import com.example.demo.entity.enums.Provider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotNull(message = "이름은 필수입니다.")
    @Size(min = 1, max =50, message = "이름은 1자에서 50자 사이여야 합니다.")
    private String name;

    @NotNull(message = "사용자 ID는 필수입니다.")
    @Size(min = 1, max = 50, message = "사용자 ID는 1자에서 50자 사이여야 합니다.")
    private String userId;

    @NotNull(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야 합니다.")
    private String password;

    @NotNull(message = "로그인 경로는 필수입니다.")
    private Provider provider;
}