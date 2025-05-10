package com.example.demo.domain.dto.gpt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequest {

    private String fairyTaleNum;
    private String tryNum;
    private String userAnswer;

}