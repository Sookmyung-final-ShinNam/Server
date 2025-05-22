package com.example.demo.domain.dto.fairyTale;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FairyTaleResponse {
    private Long id;
    private String title;
    private Boolean isFavorite;
    private LocalDateTime createdAt;
}
