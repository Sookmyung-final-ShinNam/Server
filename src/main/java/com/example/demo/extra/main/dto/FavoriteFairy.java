package com.example.demo.extra.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FavoriteFairy {
    private Long id;
    private String firstImage;
}
