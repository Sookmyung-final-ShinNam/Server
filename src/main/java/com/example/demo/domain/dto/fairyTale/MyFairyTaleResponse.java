package com.example.demo.domain.dto.fairyTale;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MyFairyTaleResponse {

    private Long id;
    private String title;
    private String content;
    private boolean isFavorite;
    private List<String> hashtags;
    private List<String> participants;
    private List<PageResponse> pages;
}
