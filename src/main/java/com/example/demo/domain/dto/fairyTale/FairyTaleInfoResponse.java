package com.example.demo.domain.dto.fairyTale;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FairyTaleInfoResponse {

    private Long id;
    private String title;
}
