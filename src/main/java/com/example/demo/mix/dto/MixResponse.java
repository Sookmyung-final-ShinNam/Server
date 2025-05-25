package com.example.demo.mix.dto;

import com.example.demo.domain.entity.enums.Gender;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MixResponse {
    private Long fairyTaleId;
    private String title;
    private String content;
}