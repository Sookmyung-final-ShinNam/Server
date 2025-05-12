package com.example.demo.domain.dto.fairy;

import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class FairyMixRequest {

    @Size(min = 2, max = 3, message = "요정 ID는 최소 2개 이상, 최대 3개까지 전달해야 합니다.")
    private List<Long> fairies;
}
