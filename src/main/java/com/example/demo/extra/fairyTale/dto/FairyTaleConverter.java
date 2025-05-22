package com.example.demo.extra.fairyTale.dto;

import com.example.demo.domain.entity.FairyTale;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FairyTaleConverter {

    public static MyFairyTaleResponse toMyFairyTaleResponse(FairyTale fairyTale) {
        // 테마 해시태그 리스트화
        List<String> hashtags = toHashtags(fairyTale);
        List<String> participants = ParticipantConverter.toFairyNames(fairyTale);
        List<PageResponse> pages = PageConverter.toPages(fairyTale);

        return MyFairyTaleResponse.builder()
                .id(fairyTale.getId())
                .title(fairyTale.getTitle())
                .content(fairyTale.getContent())
                .isFavorite(fairyTale.getIsFavorite())
                .hashtags(hashtags)
                .participants(participants)
                .pages(pages)
                .build();
    }

    private static List<String> toHashtags(FairyTale fairyTale) {
        List<String> hashtags = new ArrayList<>();

        hashtags.add(fairyTale.getTheme1());
        if (fairyTale.getTheme2() != null) hashtags.add(fairyTale.getTheme2());
        if (fairyTale.getTheme3() != null) hashtags.add(fairyTale.getTheme3());

        return hashtags;
    }
}