package com.example.demo.generate.service;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.generate.dto.FeedbackRequest;
import com.example.demo.generate.dto.StoryIntroRequest;
import com.example.demo.generate.dto.StoryRequest;

public interface ChatService {

    ApiResponse generateStoryIntro(String userId, StoryIntroRequest request, String promptFileName);

    ApiResponse generateQuestion(String userId, StoryRequest request);

    ApiResponse generateNext(String userId, StoryRequest request);

    ApiResponse provideFeedback(String userId, FeedbackRequest request);



}