package com.example.demo.domain.dto.fairyTale;

import lombok.Getter;
import java.util.List;

@Getter
public class FairyTaleRequest {
    private String title;
    private String content;
    private List<Long> fairyIds;  // 등장할 요정 ID 목록

}