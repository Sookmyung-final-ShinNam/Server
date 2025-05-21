package com.example.demo.domain.converter.fairy;

import com.example.demo.domain.dto.fairyTale.FairyTaleInfoResponse;
import com.example.demo.domain.dto.fairy.FairyInfoRequest;
import com.example.demo.domain.dto.fairy.FairyInfoResponse;
import com.example.demo.domain.dto.fairy.FairyRequest;
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
                .age(request.getAge())
                .gender(Gender.fromString(request.getGender()))
                .user(user)
                .build();
    }

    public static Fairy toEntity(FairyRequest request, User user) {
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

        List<FairyTaleInfoResponse> fairyTales = Optional.ofNullable(fairy.getParticipations())
                .orElse(List.of())
                .stream()
                .map(FairyConverter::toFairyTaleInfo)
                .collect(Collectors.toList());

        List<String> images = toImages(fairy);

        List<String> lines = Optional.ofNullable(fairy.getLines())
                .orElse(List.of())
                .stream()
                .map(FairyLine::getLine)
                .collect(Collectors.toList());

        return MyFairyResponse.builder()
                .fairyId(fairy.getId())
                .name(fairy.getName())
                .personality(fairy.getPersonality())
                .age(fairy.getAge())
                .images(images)
                .lines(lines)
                .fairyTales(fairyTales)
                .build();
    }

    private static FairyTaleInfoResponse toFairyTaleInfo(FairyParticipation participation) {
        return FairyTaleInfoResponse.builder()
                .id(participation.getFairyTale().getId())
                .title(participation.getFairyTale().getTitle())
                .build();
    }

    public static FairyInfoResponse toFairyInfoResponse(Fairy fairy) {
        return FairyInfoResponse.builder()
                .fairyId(fairy.getId())
                .name(fairy.getName())
//                .isStar(fairy.getIsStar()) // boolean 필드가 있다면
                .createdAt(fairy.getCreatedAt())
                .build();
    }

    public static List<FairyInfoResponse> toFairyInfoResponseList(List<Fairy> fairies) {
        return fairies.stream()
                .map(FairyConverter::toFairyInfoResponse)
                .collect(Collectors.toList());
    }
}