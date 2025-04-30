package com.example.demo.domain.dto.gpt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoryFeedbackRequest {

    private String context;
    private String userAnswer;

}