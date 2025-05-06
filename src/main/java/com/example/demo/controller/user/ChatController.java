package com.example.demo.controller.user;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.gpt.*;
import com.example.demo.domain.dto.FairyTale.FairyEndingRequest;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController extends BaseController {

    private final ChatService chatService;

    // 1. 비속어 교정
    @PostMapping("/correct-answer")
    public ApiResponse correctUserAnswer(@RequestBody UserAnswerCorrectionRequest request) {
        String userId = getCurrentUserId();
        return chatService.correctUserAnswer(userId, request, "correct_user_answer.json");
    }

    // 2. 유의미한 단어 추출
    @PostMapping("/analyze-text")
    public ApiResponse analyzeUserText(@RequestBody UserTextAnalysisRequest request) {
        String userId = getCurrentUserId();
        return chatService.analyzeUserText(userId, request, "find_significant_words.json");
    }

    // 3. 이야기 흐름에 맞는 피드백
    @PostMapping("/story-feedback")
    public ApiResponse provideStoryFeedback(@RequestBody StoryFeedbackRequest request) {
        String userId = getCurrentUserId();
        return chatService.provideStoryFeedback(userId, request, "story_context_helper_prompt.json");
    }

    // 4. 이야기 시작 문장 생성
    @PostMapping("/generate-story-intro")
    public ApiResponse generateStoryIntro(@RequestBody StoryIntroRequest request) {
        String userId = getCurrentUserId();
        return chatService.generateStoryIntro(userId, request, "story_intro_prompt.json");
    }

    // 5. 상황 기반 질문 생성
    @PostMapping("/generate-question")
    public ApiResponse generateQuestion(@RequestBody StoryQuestionRequest request) {
        String userId = getCurrentUserId();
        return chatService.generateQuestion(userId, request, "generate_story_question_prompt.json");
    }

    // 6. 상황 기반 엔딩 생성
    @PostMapping("/generate-fairy-ending")
    public ApiResponse generateFairyEnding(@RequestBody FairyEndingRequest request) {
        String userId = getCurrentUserId();
        return chatService.generateFairyEnding(userId, request, "fairytale_endings_prompt.json");
    }

    // 7. 주인공 특징 요약
    @PostMapping("/generate-protagonist-summary")
    public ApiResponse generateProtagonistSummary(@RequestBody ProtagonistSummaryRequest request) {
        String userId = getCurrentUserId();
        return chatService.generateProtagonistSummary(userId, request, "fairytale_character_summary.json");
    }

}