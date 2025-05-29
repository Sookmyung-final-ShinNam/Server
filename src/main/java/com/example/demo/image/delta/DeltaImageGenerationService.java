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

    public ApiResponse<?> MixFairyTale(String userId, DeltaImageRequestDto dto) {
        System.out.println("🟡 캐릭터 및 행동 이미지 생성 시작");

        // 1. 캐릭터 외형 정보 설정
        Map<String, String> characterDescriptions = new HashMap<>();
        characterDescriptions.put("지윤", "갈색 단발머리, 분홍색 원피스를 입고 밝은 미소를 짓는 어린이");
        characterDescriptions.put("유민", "검은 뿔테 안경, 깔끔한 파란 셔츠를 입고 진지한 표정을 짓는 어린이");

        // 2. 캐릭터 이미지 생성
        Map<String, String> characterImages = generateCharacterImages(characterDescriptions);

        // 3. 행동 장면 이미지 생성
        String scenePrompt = generateScenePrompt(characterDescriptions);
        String sceneImageUrl = generateImageWithGptApi(scenePrompt);

        // 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.put("characterImages", characterImages);
        result.put("sceneImage", sceneImageUrl);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    private Map<String, String> generateCharacterImages(Map<String, String> descriptions) {
        Map<String, String> characterImages = new HashMap<>();

        for (Map.Entry<String, String> entry : descriptions.entrySet()) {
            String name = entry.getKey();
            String desc = entry.getValue();
            String prompt = String.format("%s의 정면 전신 일러스트 (%s), 디즈니 스타일, 따뜻한 색감, 부드러운 선, 고화질", name, desc);
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

    private String generateScenePrompt(Map<String, String> descriptions) {
        StringBuilder sb = new StringBuilder();

        sb.append("다음은 두 어린이 캐릭터가 함께 등장하는 동화 속 한 장면이다. ");
        sb.append("따뜻한 햇살이 비추는 공원 벤치에 지윤과 유민이 나란히 앉아 있다. ");
        sb.append("지윤은 햄버거를 먹으며 유민에게 맛있다고 자랑하고 있고, 유민은 그런 지윤을 바라보며 웃고 있다. ");
        sb.append("두 아이는 즐겁게 대화를 나누며 친근한 분위기를 풍긴다. ");

        sb.append("각 캐릭터는 다음 외형을 유지한다: ");
        for (Map.Entry<String, String> entry : descriptions.entrySet()) {
            sb.append(String.format("%s: %s. ", entry.getKey(), entry.getValue()));
        }

        sb.append("디즈니 스타일의 일러스트로, 따뜻하고 밝은 색감, 애니메이션 스타일, 1024x1024 해상도.");
        return sb.toString();
    }

    public String generateImageWithGptApi(String prompt) {
        try {
            String urlStr = "https://api.openai.com/v1/images/generations";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
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