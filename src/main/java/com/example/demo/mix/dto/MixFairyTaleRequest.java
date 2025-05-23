package com.example.demo.mix.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MixFairyTaleRequest {

    @NotEmpty(message = "요정 번호는 최소 1개 이상이어야 합니다.")
    @Size(min = 1, max = 3, message = "요정 번호는 1개 이상 3개 이하여야 합니다.")
    private List<Long> fairyIds;  // 요정들의 번호 (1~3개)

    @NotNull(message = "테마는 필수 항목입니다.")
    private String themes;           // 테마

    @NotNull(message = "배경은 필수 항목입니다.")
    private String background;      // 배경

}