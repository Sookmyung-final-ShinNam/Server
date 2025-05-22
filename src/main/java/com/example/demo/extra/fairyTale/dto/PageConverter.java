package com.example.demo.extra.fairyTale.dto;

import com.example.demo.domain.entity.FairyTale;
import com.example.demo.domain.entity.Page;

import java.util.ArrayList;
import java.util.List;

public class PageConverter {

    public static List<PageResponse> toPages(FairyTale fairyTale) {
        List<Page> pageList = fairyTale.getPages();
        List<PageResponse> pages = new ArrayList<>();

        for (int i = 0; i < pageList.size(); i++) {
            Page page = pageList.get(i);
            pages.add(toPage(page, i + 1));
        }

        return pages;
    }

    private static PageResponse toPage(Page page, int seq) {
        return PageResponse.builder()
                .pageId(page.getId())
                .pageNo(seq)
                .image(page.getImage())
                .plot(page.getPlot())
                .build();
    }
}
