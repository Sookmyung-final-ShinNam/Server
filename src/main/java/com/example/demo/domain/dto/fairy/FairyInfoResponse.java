package com.example.demo.domain.dto.fairy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class FairyInfoResponse {
    private Long fairyId;
    private String name;
    private Boolean isStar;
    private LocalDateTime createdAt;
}