package com.example.demo.domain.dto.fairy;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FairyRequest {

    private String name;         // 요정 이름
    private String personality;  // 요정 성격
    private String appearance;   // 요정 외모

    private String title;        // 동화 제목
    private String content;      // 동화 내용

}