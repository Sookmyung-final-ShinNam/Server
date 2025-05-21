package com.example.demo.domain.dto.user;

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
