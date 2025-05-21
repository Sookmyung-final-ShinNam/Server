package com.example.demo.domain.dto.fairy;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FairyInfoResponse2 {
    private Long id;
    private String name;
    private Boolean isFavorite;
    private LocalDateTime createdAt;
}