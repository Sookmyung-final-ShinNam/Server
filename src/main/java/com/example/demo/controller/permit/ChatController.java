package com.example.demo.controller.permit;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.gpt.StoryFeedbackRequest;
import com.example.demo.domain.dto.gpt.StoryIntroRequest;
import com.example.demo.domain.dto.gpt.UserAnswerCorrectionRequest;
import com.example.demo.domain.dto.gpt.UserTextAnalysisRequest;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permit/chat")
public class ChatController {

    private final ChatService chatService;

    // 1. 비속어 교정
    @PostMapping("/correct-answer")
    public ApiResponse correctUserAnswer(@RequestBody UserAnswerCorrectionRequest request) {
        return chatService.correctUserAnswer(request, "correct_user_answer.json");
    }

    // 2. 유의미한 단어 추출
    @PostMapping("/analyze-text")
    public ApiResponse analyzeUserText(@RequestBody UserTextAnalysisRequest request) {
        return chatService.analyzeUserText(request, "find_significant_words.json");
    }

    // 3. 이야기 흐름에 맞는 피드백
    @PostMapping("/story-feedback")
    public ApiResponse provideStoryFeedback(@RequestBody StoryFeedbackRequest request) {
        return chatService.provideStoryFeedback(request, "story_context_helper_prompt.json");
    }

    // 4. 이야기 시작 문장 생성
    @PostMapping("/generate-story-intro")
    public ApiResponse generateStoryIntro(@RequestBody StoryIntroRequest request) {
        return chatService.generateStoryIntro(request, "story_intro_prompt.json");
    }

}