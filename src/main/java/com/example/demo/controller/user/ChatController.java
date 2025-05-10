package com.example.demo.controller.user;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.gpt.*;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController extends BaseController {

    private final ChatService chatService;

    // 1. 이야기 시작 문장 생성
    @PostMapping("/generate-story-intro")
    public ApiResponse generateStoryIntro(@RequestBody StoryIntroRequest request) {
        String userId = getCurrentUserId();
        return chatService.generateStoryIntro(userId, request, "gui_base_story_intro.json");
    }

    // 2. 상황 기반 질문 생성
    @PostMapping("/generate-question")
    public ApiResponse generateQuestion(@RequestBody StoryRequest request) {
        String userId = getCurrentUserId();
        return chatService.generateQuestion(userId, request);
    }

    // 3. 상황 기반 다음 이야기 생성
    @PostMapping("/generate-next")
    public ApiResponse generateNext(@RequestBody StoryRequest request) {
        String userId = getCurrentUserId();
        return chatService.generateNext(userId, request);
    }

    // 4. 상황 기반으로 사용자 답변 피드백
    @PostMapping("/provide-feedback")
    public ApiResponse provideFeedback(@RequestBody FeedbackRequest request) {
        String userId = getCurrentUserId();
        return chatService.provideFeedback(userId, request);
    }


}