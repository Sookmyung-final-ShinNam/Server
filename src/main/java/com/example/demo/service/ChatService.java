package com.example.demo.service;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.FairyTale.FairyEndingRequest;
import com.example.demo.domain.dto.gpt.*;

public interface ChatService {

    ApiResponse generateStoryIntro(String userId, StoryIntroRequest request, String promptFileName);

    ApiResponse generateQuestion(String userId, StoryRequest request);

    ApiResponse generateNext(String userId, StoryRequest request);

    ApiResponse provideFeedback(String userId, FeedbackRequest request);


}