package com.example.demo.image.delta;

import lombok.Data;

@Data
public class DeltaImageRequestDto {
    private Long fairyId; // 요정 번호
    private Long fairyTaleId; // 탄생 동화 번호

    private String appearance;
    private String behavior; // behavior prompt + optional custom behavior
}