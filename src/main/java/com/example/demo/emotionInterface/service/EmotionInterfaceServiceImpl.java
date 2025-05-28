package com.example.demo.emotionInterface.service;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.base.api.status.SuccessStatus;
import com.example.demo.base.util.PromptLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionInterfaceServiceImpl implements EmotionInterfaceService {

    private final PromptLoader promptLoader;

    @Value("${chatgpt.api-key}")
    private String apiKey;

    @Override
    public ApiResponse  emotionHtml(String userId, String text) {
        String bodyTemplate = promptLoader.loadPrompt("group_words_by_emotion_font-image.json");
        String body = bodyTemplate.replace("{text}", text);

        String answer = callChatGpt(body); // 이 answer는 HTML 일부가 포함된 문자열
        System.out.println(answer);

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);
    }



    // 공통 부분 : gpt 호출
    public String callChatGpt(String finalPromptJson) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);


            // ✅ JSON 문자열이 유효한지 파싱해서 확인 (선택적 유효성 체크)
            ObjectMapper mapper = new ObjectMapper();
            try {
                // 줄바꿈 문자 제거 (가장 안전한 방식)
                finalPromptJson = finalPromptJson.replaceAll("[\\n\\r]+", " ");
                System.out.println("🔎 전달된 프롬프트(JSON):\n" + finalPromptJson);

                JsonNode requestNode = mapper.readTree(finalPromptJson);
                System.out.println("✅ JSON 파싱 성공: " + requestNode.toPrettyString());
            } catch (JsonProcessingException e) {
                System.err.println("❌ JSON 파싱 실패: " + e.getMessage());
                throw new CustomException(ErrorStatus.JSON_PARSE_ERROR);
            }

            System.out.println("✅ JSON 형식 확인 완료");

            // ✅ JSON 그대로 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = finalPromptJson.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // ✅ 응답 상태 코드 확인
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }

                    JsonNode responseJson = mapper.readTree(response.toString());
                    return responseJson.get("choices").get(0).get("message").get("content").asText();
                }
            } else {
                // ✅ 오류 응답 처리
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line.trim());
                    }

                    System.err.println("❌ ChatGPT 오류 응답 코드: " + responseCode);
                    System.err.println("❌ ChatGPT 오류 메시지: " + errorResponse);

                    // 오류 메시지 파싱해서 사용자에게 안내할 수도 있음
                    JsonNode errorJson = mapper.readTree(errorResponse.toString());
                    String errorMessage = errorJson.path("error").path("message").asText();
                    throw new CustomException(ErrorStatus.CHAT_GPT_API_CALL_FAILED);
                }
            }

        } catch (IOException e) {
            log.error("❌ ChatGPT 호출 중 예외 발생: {}", e.getMessage());
            throw new CustomException(ErrorStatus.CHAT_GPT_API_CALL_FAILED);
        }
    }



}