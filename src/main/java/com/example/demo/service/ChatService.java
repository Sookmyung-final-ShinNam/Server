package com.example.demo.service;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.gpt.StoryFeedbackRequest;
import com.example.demo.domain.dto.gpt.StoryIntroRequest;
import com.example.demo.domain.dto.gpt.UserAnswerCorrectionRequest;
import com.example.demo.domain.dto.gpt.UserTextAnalysisRequest;

public interface ChatService {

    ApiResponse correctUserAnswer(UserAnswerCorrectionRequest request, String promptFileName);

    ApiResponse analyzeUserText(UserTextAnalysisRequest request, String promptFileName);

    ApiResponse provideStoryFeedback(StoryFeedbackRequest request, String promptFileName);

    ApiResponse generateStoryIntro(StoryIntroRequest request, String promptFileName);

}