package com.example.demo.image.delta;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.status.SuccessStatus;
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
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeltaImageGenerationService {

    @Value("${chatgpt.api-key}")
    private String apiKey;

    /**
     * 캐릭터 2명을 생성하고, 이들이 특정 행동을 하는 이미지를 생성함
     */
    public ApiResponse<?> MixFairyTale(String userId, DeltaImageRequestDto dto) {
        System.out.println("🟡 캐릭터 및 행동 이미지 생성 시작");

        // 1. 캐릭터 이미지 2개 생성
        Map<String, String> characterImages = generateCharacterImages(List.of("지윤", "유민"));

        // 2. 캐릭터들이 특정 행동을 하는 장면 이미지 생성
        String scenePrompt = "지윤이는 햄버거를 먹고 있고, 유민이는 공부를 하고 있음. 두 캐릭터는 이전 이미지에서 본 모습(헤어스타일, 의상, 표정 등)을 유지해야 함.";
        String sceneImageUrl = generateSceneImage(characterImages, scenePrompt);

        // 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.put("characterImages", characterImages);
        result.put("sceneImage", sceneImageUrl);

        return ApiResponse.of(SuccessStatus._OK, result);
    }

    /**
     * 캐릭터 이미지 생성
     */
    private Map<String, String> generateCharacterImages(List<String> names) {
        Map<String, String> characterImages = new HashMap<>();

        for (String name : names) {
            String prompt = String.format("밝고 귀여운 스타일의 어린이 캐릭터 %s의 정면 전신 일러스트, 디즈니 스타일", name);
            System.out.println("🧒 캐릭터 프롬프트: " + prompt);
            String imageUrl = generateImageWithGptApi(prompt);
            if (imageUrl != null) {
                characterImages.put(name, imageUrl);
            } else {
                log.warn("❌ {} 캐릭터 이미지 생성 실패", name);
            }
        }
        return characterImages;
    }

    /**
     * 캐릭터 행동 장면 이미지 생성
     */
    private String generateSceneImage(Map<String, String> characterImages, String basePrompt) {
        StringBuilder promptBuilder = new StringBuilder(basePrompt);

        for (Map.Entry<String, String> entry : characterImages.entrySet()) {
            promptBuilder.append(String.format(" %s는 이전 이미지에서 본 모습(헤어스타일, 의상, 표정 등)을 유지해야 함.", entry.getKey()));
        }

        String finalPrompt = promptBuilder.toString();
        System.out.println("🎬 행동 이미지 프롬프트: " + finalPrompt);
        return generateImageWithGptApi(finalPrompt);
    }

    /**
     * OpenAI API 호출 - DALL·E 3 기반 이미지 생성
     */
    public String generateImageWithGptApi(String prompt) {
        try {
            String urlStr = "https://api.openai.com/v1/images/generations";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 요청 바디 구성
            String requestBody = String.format("""
                {
                    "model": "dall-e-3",
                    "prompt": "%s",
                    "n": 1,
                    "size": "1024x1024"
                }
            """, prompt.replace("\"", "\\\""));

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode json = mapper.readTree(response.toString());
                    return json.get("data").get(0).get("url").asText();
                }
            } else {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line.trim());
                    }
                    log.error("GPT 이미지 생성 실패 응답 코드: {}, 메시지: {}", responseCode, errorResponse.toString());
                }
                return null;
            }
        } catch (IOException e) {
            log.error("GPT 이미지 생성 중 예외 발생: {}", e.getMessage());
            return null;
        }
    }

}