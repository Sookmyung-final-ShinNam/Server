package com.example.demo.domain.dto.fairy;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FairyInfoResponse {
    private Long fairyId;          // 요정 ID
    private String name;           // 요정 이름
    private String appearance;     // 외모
    private String personality;    // 성격
    private Integer age;           // 나이
    private String gender;         // 성별
    private boolean isFavorite;    // 즐겨찾기 여부
    private LocalDateTime createdAt;  // 생성 시간

}

