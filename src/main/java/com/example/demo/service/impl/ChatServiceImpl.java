package com.example.demo.service.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.fairy.FairyRequest;
import com.example.demo.domain.dto.gpt.*;
import com.example.demo.entity.base.FairyTale;
import com.example.demo.repository.FairyTaleRepository;
import com.example.demo.service.ChatService;
import com.example.demo.service.FairyService;
import com.example.demo.util.PromptLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final PromptLoader promptLoader;

    @Value("${chatgpt.api-key}")
    private String apiKey;

    @Autowired
    private FairyService fairyService;

    @Autowired
    private FairyTaleRepository fairyTaleRepository;

    @Override
    public ApiResponse correctUserAnswer(String userId, UserAnswerCorrectionRequest request, String promptFileName) {
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        String body = bodyTemplate
                .replace("{user_answer}", request.getUserAnswer());

        String answer = callChatGpt(body);

        // 결과 저장 및 응답 처리
        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);
    }

    @Override
    public ApiResponse analyzeUserText(String userId, UserTextAnalysisRequest request, String promptFileName) {
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        String body = bodyTemplate
                .replace("{user_answer}", request.getUserAnswer());

        String answer = callChatGpt(body);

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);
    }

    @Override
    public ApiResponse provideStoryFeedback(String userId, StoryFeedbackRequest request, String promptFileName) {
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        String body = bodyTemplate
                .replace("{context}", request.getContext())
                .replace("{user_answer}", request.getUserAnswer());

        // GPT 호출
        String answer = callChatGpt(body);

        // GPT의 응답을 파싱
        boolean isAppropriateAnswer = false;
        String result = "";

        // '맞아' 또는 '아니야'로 시작하는지 확인
        if (answer.startsWith("맞아")) {
            isAppropriateAnswer = true;
            result = answer.substring(4);  // '맞아' 이후의 내용을 추출
        } else if (answer.startsWith("아니야")) {
            isAppropriateAnswer = false;
            result = answer.substring(5);  // '아니야' 이후의 내용을 추출
        }

        // StoryFeedbackResult 객체 생성
        StoryFeedbackResult parsedResult = new StoryFeedbackResult(result, isAppropriateAnswer);

        // ApiResponse로 반환
        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, parsedResult);
    }

    @Override
    public ApiResponse generateStoryIntro(String userId, StoryIntroRequest request, String promptFileName) {
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        String body = bodyTemplate
                .replace("{themes}", request.getThemes())
                .replace("{backgrounds}", request.getBackgrounds())
                .replace("{name}", request.getName())
                .replace("{gender}", request.getGender())
                .replace("{age}", String.valueOf(request.getAge()))
                .replace("{hair_color}", request.getHairColor())
                .replace("{eye_color}", request.getEyeColor())
                .replace("{hair_style}", request.getHairStyle());

        String answer = callChatGpt(body);

        String title = String.format("주제: %s, 배경: %s", request.getThemes(), request.getBackgrounds());
        String appearance = String.format("성별: %s, 나이: %d, 머리 색상: %s, 눈 색상: %s, 머리스타일: %s",
                request.getGender(), request.getAge(), request.getHairColor(), request.getEyeColor(), request.getHairStyle());

        FairyRequest fairyRequest = FairyRequest.builder()
                .name(request.getName())
                .personality("착함")
                .appearance(appearance)
                .title(title)
                .content(answer)
                .build();

        // 이미 ApiResponse 타입이라면 바로 반환
        return fairyService.createFairy(userId, fairyRequest);
    }

    @Override
    public ApiResponse generateQuestion(String userId, StoryQuestionRequest request, String promptFileName) {
        // prompt 템플릿 로딩
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        // 동화 번호로 FairyTale 엔티티 조회
        FairyTale fairyTale = fairyTaleRepository.findById(Long.parseLong(request.getFairyTaleNum()))
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_TALE_NOT_FOUND));

        // 동화의 content를 situation으로 설정
        String body = bodyTemplate.replace("{situation}", fairyTale.getContent());

        // GPT 호출
        String answer = callChatGpt(body);

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);
    }

    // 공통 부분 : gpt 호출
    private String callChatGpt(String body) {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.toString());
                return jsonNode.get("choices").get(0).get("message").get("content").asText();
            }

        } catch (IOException e) {
            throw new CustomException(ErrorStatus.CHAT_GPT_API_CALL_FAILED);
        }
    }

}