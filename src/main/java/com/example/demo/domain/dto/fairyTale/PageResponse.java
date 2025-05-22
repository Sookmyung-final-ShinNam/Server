package com.example.demo.domain.dto.fairyTale;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponse {

    private Long id;
    private Integer pageNo;
    private String plot;
    private String image;
}
