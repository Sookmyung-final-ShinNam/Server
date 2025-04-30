package com.example.demo.domain.converter.gpt;

import com.example.demo.domain.dto.gpt.UserAnswerCorrectionRequest;
import com.example.demo.entity.base.gpt.UserAnswerCorrection;
import org.springframework.stereotype.Component;

@Component
public class UserAnswerCorrectionConverter {
    public UserAnswerCorrection toEntity(UserAnswerCorrectionRequest dto) {
        UserAnswerCorrection entity = new UserAnswerCorrection();
        entity.setUserAnswer(dto.getUserAnswer());
        return entity;
    }
}