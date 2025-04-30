package com.example.demo.domain.converter.gpt;

import com.example.demo.domain.dto.gpt.UserTextAnalysisRequest;
import com.example.demo.entity.base.gpt.UserTextAnalysis;
import org.springframework.stereotype.Component;

@Component
public class UserTextAnalysisConverter {
    public UserTextAnalysis toEntity(UserTextAnalysisRequest dto) {
        UserTextAnalysis entity = new UserTextAnalysis();
        entity.setUserAnswer(dto.getUserAnswer());
        return entity;
    }
}