package com.example.demo.domain.converter.fairy;

import com.example.demo.domain.dto.fairy.FairyInfoResponse2;
import com.example.demo.domain.dto.fairyTale.FairyTaleInfoResponse;
import com.example.demo.domain.dto.fairy.FairyInfoRequest;
import com.example.demo.domain.dto.fairy.FairyInfoResponse;
import com.example.demo.domain.dto.fairy.MyFairyResponse;
import com.example.demo.domain.dto.user.FavoriteFairy;
import com.example.demo.entity.base.*;
import com.example.demo.entity.enums.Gender;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FairyConverter {

    public static Fairy toEntity(FairyInfoRequest request, User user) {
        return Fairy.builder()
                .name(request.getName())
                .personality(request.getPersonality())
                .appearance(request.getAppearance())
                .age(request.getAge())
                .gender(Gender.valueOf(request.getGender().toUpperCase()))
                .user(user)
                .build();
    }

    public static List<String> toImages(Fairy fairy) {
        return  Optional.ofNullable(fairy.getImages())
                .orElse(List.of())
                .stream()
                .map(FairyImage::getImage)
                .collect(Collectors.toList());
    }

    public static MyFairyResponse toMyFairyResponse(Fairy fairy) {

        // 동화 참여 정보 변환
        List<FairyTaleInfoResponse> fairyTales = Optional.ofNullable(fairy.getParticipations())
                .orElse(List.of())
                .stream()
                .map(FairyConverter::toFairyTaleInfo)
                .collect(Collectors.toList());

        // 요정 이미지 리스트 추출
        List<String> images = Optional.ofNullable(fairy.getImages())
                .orElse(List.of())
                .stream()
                .map(FairyImage::getImage)
                .collect(Collectors.toList());


        // 요정 대사 리스트 추출
        List<String> lines = Optional.ofNullable(fairy.getLines())
                .orElse(List.of())
                .stream()
                .map(FairyLine::getLine)
                .collect(Collectors.toList());

        // 최종 DTO 생성
        return MyFairyResponse.builder()
                .fairyId(fairy.getId())
                .name(fairy.getName())
                .personality(fairy.getPersonality())
                .appearance(fairy.getAppearance())
                .age(fairy.getAge())
                .gender(fairy.getGender().toString())
                .firstImage(fairy.getFirstImage())
                .isFavorite(fairy.getIsFavorite())
                .images(images)
                .lines(lines)
                .fairyTales(fairyTales)
                .build();
    }


    // 요정이 출연한 동화 정보 DTO
    private static FairyTaleInfoResponse toFairyTaleInfo(FairyParticipation participation) {
        return FairyTaleInfoResponse.builder()
                .id(participation.getFairyTale().getId())
                .title(participation.getFairyTale().getTitle())
                .build();
    }

    public static List<FairyInfoResponse2> toFairyInfosResponse(List<Fairy> fairies) {
        return fairies.stream()
                .map(f -> FairyInfoResponse2.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .firstImage(f.getFirstImage())
                        .isFavorite(f.getIsFavorite())
                        .createdAt(f.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public static  List<FavoriteFairy> toFavoriteFairiesResponse(List<Fairy> fairies) {
        return fairies.stream()
                .map(f -> FavoriteFairy.builder()
                        .id(f.getId())
                        .firstImage(f.getFirstImage())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }
}