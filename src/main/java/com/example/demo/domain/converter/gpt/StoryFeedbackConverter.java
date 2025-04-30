package com.example.demo.domain.converter.gpt;

import com.example.demo.domain.dto.gpt.StoryFeedbackRequest;
import com.example.demo.entity.base.gpt.StoryFeedback;
import org.springframework.stereotype.Component;

@Component
public class StoryFeedbackConverter {
    public StoryFeedback toEntity(StoryFeedbackRequest dto) {
        StoryFeedback entity = new StoryFeedback();
        entity.setContext(dto.getContext());
        entity.setUserAnswer(dto.getUserAnswer());
        return entity;
    }
}