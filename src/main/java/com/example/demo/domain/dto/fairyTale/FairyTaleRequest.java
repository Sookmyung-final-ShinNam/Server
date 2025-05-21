package com.example.demo.domain.dto.fairyTale;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FairyTaleRequest {
    private String title;   // 동화 제목
    private String content; // 동화 내용
}