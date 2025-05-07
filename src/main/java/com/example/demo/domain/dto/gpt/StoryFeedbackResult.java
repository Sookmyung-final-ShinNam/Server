package com.example.demo.domain.dto.gpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoryFeedbackResult {
    private String feedbackText;
    private boolean appropriate;
}