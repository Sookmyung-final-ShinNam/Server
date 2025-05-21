package com.example.demo.domain.dto.fairy;

import com.example.demo.domain.dto.fairyTale.FairyTaleInfoResponse;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyFairyResponse {
    private Long fairyId;                     // 요정 ID
    private String name;                      // 요정 이름
    private String personality;               // 성격
    private String appearance;                // 외모
    private Integer age;                      // 나이
    private String gender;                    // 성별
    private boolean isFavorite;               // 즐겨찾기 여부
    private List<String> images;              // 요정 이미지 리스트
    private List<String> lines;               // 요정 대사 리스트
    private List<FairyTaleInfoResponse> fairyTales;  // 요정이 등장한 동화 정보
}