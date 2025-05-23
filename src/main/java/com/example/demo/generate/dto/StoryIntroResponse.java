package com.example.demo.generate.dto;

import com.example.demo.extra.fairyTale.dto.FairyTaleInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StoryIntroResponse {
    private String plot;
    private String emotionText;
}