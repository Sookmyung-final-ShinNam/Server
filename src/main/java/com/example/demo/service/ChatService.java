package com.example.demo.service;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.FairyTale.FairyEndingRequest;
import com.example.demo.domain.dto.gpt.StoryFeedbackRequest;
import com.example.demo.domain.dto.gpt.StoryIntroRequest;
import com.example.demo.domain.dto.gpt.UserAnswerCorrectionRequest;
import com.example.demo.domain.dto.gpt.StoryQuestionRequest;
import com.example.demo.domain.dto.gpt.UserTextAnalysisRequest;

public interface ChatService {

    ApiResponse correctUserAnswer(String userId, UserAnswerCorrectionRequest request, String promptFileName);

    ApiResponse analyzeUserText(String userId, UserTextAnalysisRequest request, String promptFileName);

    ApiResponse provideStoryFeedback(String userId, StoryFeedbackRequest request, String promptFileName);

    ApiResponse generateStoryIntro(String userId, StoryIntroRequest request, String promptFileName);

    ApiResponse generateQuestion(String userId, StoryQuestionRequest request, String promptFileName);

    ApiResponse generateFairyEnding(String userId, FairyEndingRequest request, String promptFileName);

}