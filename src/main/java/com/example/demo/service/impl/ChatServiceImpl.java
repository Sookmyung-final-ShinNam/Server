package com.example.demo.service.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.gpt.StoryFeedbackRequest;
import com.example.demo.domain.dto.gpt.StoryIntroRequest;
import com.example.demo.domain.dto.gpt.UserAnswerCorrectionRequest;
import com.example.demo.domain.dto.gpt.UserTextAnalysisRequest;
import com.example.demo.service.ChatService;
import com.example.demo.util.PromptLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

    @Override
    public ApiResponse correctUserAnswer(UserAnswerCorrectionRequest request, String promptFileName) {
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        String body = bodyTemplate
                .replace("{user_answer}", request.getUserAnswer());

        String answer = callChatGpt(body);

        // 결과 저장 및 응답 처리
        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);
    }

    @Override
    public ApiResponse analyzeUserText(UserTextAnalysisRequest request, String promptFileName) {
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        String body = bodyTemplate
                .replace("{user_answer}", request.getUserAnswer());

        String answer = callChatGpt(body);

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);
    }

    @Override
    public ApiResponse provideStoryFeedback(StoryFeedbackRequest request, String promptFileName) {
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        String body = bodyTemplate
                .replace("{context}", request.getContext())
                .replace("{user_answer}", request.getUserAnswer());

        String answer = callChatGpt(body);

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);
    }

    @Override
    public ApiResponse generateStoryIntro(StoryIntroRequest request, String promptFileName) {
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