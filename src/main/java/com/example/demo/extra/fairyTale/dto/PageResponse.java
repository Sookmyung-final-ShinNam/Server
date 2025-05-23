package com.example.demo.extra.fairyTale.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponse {

    private Long pageId;
    private Integer pageNo;
    private String plot;
    private String emotionText;
    private String image;
}
