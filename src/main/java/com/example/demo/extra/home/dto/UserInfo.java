package com.example.demo.extra.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserInfo {
    private Long userId;
    private String nickname;
    private Integer userPoint;
    private Integer userFairyNum;
    private Integer maxFairyNum;
    private Integer userFairyTaleNum;
    private Integer maxFairyTaleNum;
    private List<FavoriteFairy> favoriteFairies;
}
