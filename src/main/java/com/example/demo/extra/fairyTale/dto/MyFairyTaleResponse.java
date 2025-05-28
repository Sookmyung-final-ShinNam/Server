package com.example.demo.extra.fairyTale.dto;

import com.example.demo.domain.entity.enums.Type;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyFairyTaleResponse {

    private Long id;
    private String title;
    private String content;
    private boolean isFavorite;
    private List<String> hashtags;
    private List<ParticipantResponse> participants;
    private List<PageResponse> pages;
}
