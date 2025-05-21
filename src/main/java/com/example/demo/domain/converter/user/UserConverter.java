package com.example.demo.domain.converter.user;

import com.example.demo.domain.dto.user.FavoriteFairy;
import com.example.demo.domain.dto.user.UserInfo;
import com.example.demo.entity.base.User;

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
