package com.example.demo.image.delta;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.status.SuccessStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeltaImageGenerationService {

    @Value("${chatgpt.api-key}")
    private String openAiApiKey;

    @Value("${replicate.api-key}")
    private String replicateApiKey;

    // Replicate flux-kontext-pro 모델 버전 (Replicate 모델 상세 페이지 참고)
    private final String REPLICATE_MODEL_VERSION = "9f03d99b0eafbd233e4f7e42571bc00ab6c6c03a4ca02c39a815a2e1201f4f40";

    public ApiResponse<?> MixFairyTale(String userId, DeltaImageRequestDto dto) {
        log.info("🟡 캐릭터 및 행동 이미지 생성 시작");

        // 1. 캐릭터 외형 설명
        Map<String, String> characterDescriptions = new HashMap<>();
        characterDescriptions.put("지윤", "갈색 단발머리, 분홍색 원피스를 입고 밝은 미소를 짓는 어린이");
        characterDescriptions.put("유민", "검은 뿔테 안경, 깔끔한 파란 셔츠를 입고 진지한 표정을 짓는 어린이");

        // 2. 캐릭터 기본 이미지 생성 (OpenAI DALL·E 3)
        Map<String, String> characterImages = generateCharacterImages(characterDescriptions);

        // 3. 변형 프롬프트 예시
        String prompt1 = "유민이는 밥을 먹고, 지윤이는 유민이가 밥먹는 것을 쳐다보고 있었다.";
        String prompt2 = "유민이는 수영을 하고 지윤이는 모래성을 만들고 있었다.";

        // 4. flux-kontext-pro 모델로 변형 이미지 생성
        Map<String, String> sceneImages = new HashMap<>();
        sceneImages.put("scene1", generateImageWithReplicateFlux(characterImages, prompt1));
        sceneImages.put("scene2", generateImageWithReplicateFlux(characterImages, prompt2));

        // 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.put("characterImages", characterImages);
        result.put("sceneImages", sceneImages);

        return ApiResponse.of(SuccessStatus._OK, result);
    }

    private Map<String, String> generateCharacterImages(Map<String, String> descriptions) {
        Map<String, String> characterImages = new HashMap<>();

        for (Map.Entry<String, String> entry : descriptions.entrySet()) {
            String name = entry.getKey();
            String desc = entry.getValue();
            String prompt = String.format("%s의 정면 전신 일러스트 (%s), 디즈니 스타일, 따뜻한 색감, 부드러운 선, 고화질", name, desc);
            log.info("🧒 캐릭터 프롬프트: {}", prompt);
            String imageUrl = generateImageWithOpenAi(prompt);
            if (imageUrl != null) {
                characterImages.put(name, imageUrl);
            } else {
                log.warn("❌ {} 캐릭터 이미지 생성 실패", name);
            }
        }

        return characterImages;
    }

    private String generateImageWithOpenAi(String prompt) {
        try {
            String urlStr = "https://api.openai.com/v1/images/generations";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + openAiApiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

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
                    log.error("OpenAI 이미지 생성 실패 코드: {}, 메시지: {}", responseCode, errorResponse.toString());
                }
                return null;
            }
        } catch (IOException e) {
            log.error("OpenAI 이미지 생성 중 예외 발생: {}", e.getMessage());
            return null;
        }
    }

    private String generateImageWithReplicateFlux(Map<String, String> characterImages, String scenePrompt) {
        // 간단히 첫 캐릭터 이미지로 변형 진행
        String baseImageUrl = characterImages.values().iterator().next();

        try {
            String urlStr = "https://api.replicate.com/v1/predictions";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Token " + replicateApiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "wait");  // 기다림 옵션
            conn.setDoOutput(true);

            String requestBody = String.format("""
                {
                  "version": "%s",
                  "input": {
                    "input_image": "%s",
                    "prompt": "%s",
                    "aspect_ratio": "1:1"
                  }
                }
                """, REPLICATE_MODEL_VERSION, baseImageUrl, scenePrompt.replace("\"", "\\\""));

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode json = mapper.readTree(response.toString());
                    JsonNode outputNode = json.get("prediction").get("output");
                    if (outputNode != null && outputNode.isArray() && outputNode.size() > 0) {
                        return outputNode.get(0).asText();
                    } else {
                        log.error("Replicate 응답에서 이미지 URL을 찾을 수 없음");
                    }
                }
            } else {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line.trim());
                    }
                    log.error("Replicate 이미지 변형 실패 코드: {}, 메시지: {}", responseCode, errorResponse.toString());
                }
            }
        } catch (IOException e) {
            log.error("Replicate 이미지 변형 중 예외 발생: {}", e.getMessage());
        }
        return null;
    }

}
