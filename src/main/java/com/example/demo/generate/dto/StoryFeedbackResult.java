package com.example.demo.generate.dto;

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