package com.example.demo.extra.fairyTale.dto;

import com.example.demo.domain.entity.enums.Type;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FairyTaleInfoResponse {

    private Long id;
    private String title;
    private Type type;
}
