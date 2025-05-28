package com.example.demo.extra.fairyTale.dto;

import com.example.demo.domain.entity.enums.Type;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FairyTaleResponse {
    private Long id;
    private String title;
    private Boolean isFavorite;
    private Type type;
    private LocalDateTime createdAt;
}
