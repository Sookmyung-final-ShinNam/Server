package com.example.demo.domain.converter.fairyTale;

import com.example.demo.domain.dto.fairyTale.FairyTaleRequest;
import com.example.demo.domain.dto.fairyTale.MyFairyTaleResponse;
import com.example.demo.domain.dto.fairyTale.PageResponse;
import com.example.demo.entity.base.FairyTale;
import com.example.demo.entity.base.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FairyTaleConverter {

    public FairyTale toEntity(FairyTaleRequest request, User user) {
        return FairyTale.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();
    }

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