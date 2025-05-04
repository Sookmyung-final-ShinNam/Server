package com.example.demo.domain.dto.fairy;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FairyResponse {
    private Long id;
    private String name;
    private String personality;
    private String appearance;

    // 동화 정보 추가
    private Long fairyTaleId;
    private String fairyTaleTitle;
    private String fairyTaleContent;

}