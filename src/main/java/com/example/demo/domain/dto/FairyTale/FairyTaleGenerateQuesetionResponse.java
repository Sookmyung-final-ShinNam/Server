package com.example.demo.domain.dto.FairyTale;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FairyTaleGenerateQuesetionResponse {

    private String answer;
    private String content;

    // 생성자
    public FairyTaleGenerateQuesetionResponse(String answer, String content) {
        this.answer = answer;
        this.content = content;
    }

}