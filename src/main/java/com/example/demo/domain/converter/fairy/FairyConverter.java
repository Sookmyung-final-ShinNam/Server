package com.example.demo.domain.converter.fairy;

import com.example.demo.domain.dto.fairyTale.FairyTaleInfoResponse;
import com.example.demo.domain.dto.fairy.FairyInfoRequest;
import com.example.demo.domain.dto.fairy.FairyInfoResponse;
import com.example.demo.domain.dto.fairy.MyFairyResponse;
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
                .isFavorite(Boolean.TRUE.equals(fairy.getIsFavorite()))
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


    // 요정 리스트 조회
    public static FairyInfoResponse toFairyInfoResponse(Fairy fairy) {
        return FairyInfoResponse.builder()
                .fairyId(fairy.getId())
                .name(fairy.getName())
                .appearance(fairy.getAppearance())
                .personality(fairy.getPersonality())
                .age(fairy.getAge())
                .gender(fairy.getGender().toString())
                .isFavorite(Boolean.TRUE.equals(fairy.getIsFavorite()))
                .createdAt(fairy.getCreatedAt())
                .build();
    }


    public static List<FairyInfoResponse> toFairyInfoResponseList(List<Fairy> fairies) {
        return fairies.stream()
                .map(FairyConverter::toFairyInfoResponse)
                .collect(Collectors.toList());
    }

}