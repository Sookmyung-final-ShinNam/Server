package com.example.demo.domain;

import com.example.demo.domain.dto.user.FavoriteFairy;
import com.example.demo.entity.base.Fairy;

import java.util.List;

import static com.example.demo.domain.converter.fairy.FairyConverter.toImages;

public class FavoriteConveter {

    private static FavoriteFairy toFavoriteFairy(Fairy fairy) {
        return FavoriteFairy.builder()
                .id(fairy.getId())
                .firstImage(fairy.getFirstImage())
                .build();
    }

    private static List<FavoriteFairy> toFavoriteFairies() {
        return null;
    }
}
