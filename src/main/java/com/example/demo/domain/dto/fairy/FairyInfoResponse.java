package com.example.demo.domain.dto.fairy;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class FairyInfoResponse {
    private Long id;
    private String name;
    private Boolean isFavorite;
    private LocalDateTime createdAt;
}