package com.example.demo.extra.home.dto;

import com.example.demo.domain.entity.User;

import java.util.List;

public class UserConverter {
    public static UserInfo toUserInfo(User user, List<FavoriteFairy> favoriteFairies) {
        return UserInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .userPoint(user.getPoint())
                .userFairyNum(user.getFairies().size())
                .maxFairyNum(user.getMaxFairyNum())
                .userFairyTaleNum(user.getFairyTales().size())
                .maxFairyTaleNum(user.getMaxFairyTaleNum())
                .favoriteFairies(favoriteFairies)
                .build();
    }
}
