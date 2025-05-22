package com.example.demo.extra.fairy.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FairyInfoResponse2 {
    private Long id;
    private String name;
    private String firstImage;
    private Boolean isFavorite;
    private LocalDateTime createdAt;
}