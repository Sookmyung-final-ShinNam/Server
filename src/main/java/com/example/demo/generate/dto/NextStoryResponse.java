package com.example.demo.generate.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NextStoryResponse {
    private String plot;
    private String emotionText;
}