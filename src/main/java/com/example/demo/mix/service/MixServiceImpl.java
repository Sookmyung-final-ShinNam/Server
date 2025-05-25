package com.example.demo.mix.service;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.base.api.status.SuccessStatus;
import com.example.demo.base.util.PromptLoader;
import com.example.demo.domain.entity.*;
import com.example.demo.domain.entity.enums.Type;
import com.example.demo.domain.repository.*;
import com.example.demo.emotionInterface.service.EmotionInterfaceService;
import com.example.demo.mix.dto.MixFairyTaleRequest;
import com.example.demo.mix.dto.MixResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MixServiceImpl implements MixService {

    private final PromptLoader promptLoader;

    @Value("${chatgpt.api-key}")
    private String apiKey;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PageRepository pageRepository;


    @Autowired
    private FairyParticipationRepository fairyParticipationRepository;

    @Autowired
    private FairyTaleRepository fairyTaleRepository;

    @Autowired
    private FairyRepository fairyRepository;

    @Autowired
    private EmotionInterfaceService emotionInterfaceService;


    @Override
    @Transactional
    public ApiResponse mixFairyTale(String userId, MixFairyTaleRequest request) {
        // 1. GPT 프롬프트 생성 및 호출
        String bodyTemplate = promptLoader.loadPrompt("mix_fairyTale.json");

        List<Fairy> fairies = fairyRepository.findAllById(request.getFairyIds());

        Fairy fairy1 = fairies.size() > 0 ? fairies.get(0) : null;
        Fairy fairy2 = fairies.size() > 1 ? fairies.get(1) : null;
        Fairy fairy3 = fairies.size() > 2 ? fairies.get(2) : null;

        String mixSetting = String.format(
                "사용자 설정 : 테마: %s, 배경: %s, 요정1: (이름=%s, 나이=%d세, 성별=%s, 성격=%s, 헤어컬러=%s, 눈 색=%s, 헤어스타일=%s)%s%s",
                request.getThemes(),
                request.getBackground(),
                fairy1.getName(),
                fairy1.getAge(),
                fairy1.getGender(),
                fairy1.getPersonality(),
                fairy1.getHairColor(),
                fairy1.getEyeColor(),
                fairy1.getHairStyle(),
                fairy2 != null ? String.format(", 요정2: (이름=%s, 나이=%d세, 성별=%s, 성격=%s, 헤어컬러=%s, 눈 색=%s, 헤어스타일=%s)",
                        fairy2.getName(),
                        fairy2.getAge(),
                        fairy2.getGender(),
                        fairy2.getPersonality(),
                        fairy2.getHairColor(),
                        fairy2.getEyeColor(),
                        fairy2.getHairStyle()) : "",
                fairy3 != null ? String.format(", 요정3: (이름=%s, 나이=%d세, 성별=%s, 성격=%s, 헤어컬러=%s, 눈 색=%s, 헤어스타일=%s)",
                        fairy3.getName(),
                        fairy3.getAge(),
                        fairy3.getGender(),
                        fairy3.getPersonality(),
                        fairy3.getHairColor(),
                        fairy3.getEyeColor(),
                        fairy3.getHairStyle()) : ""
        );

        String body = bodyTemplate.replace("{mixSetting}", mixSetting);

        String answer = callChatGpt(body);

        System.out.println(answer);


// 1. 파싱
        String title = extractByPattern(answer, "제목\\s*:\\s*\"?(.*?)\"?$");

        System.out.println("[파싱된 제목] " + title);

        List<String> scenes = extractScenes(answer);
        System.out.println("[파싱된 장면 수] " + scenes.size());
        for (int i = 0; i < scenes.size(); i++) {
            System.out.println("Scene " + (i + 1) + ": " + scenes.get(i));
        }


// 2. 사용자 조회
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

// 3. 테마 분리
        String[] themes = request.getThemes().split("\\s*,\\s*");
        String theme1 = themes.length > 0 ? themes[0] : null;
        String theme2 = themes.length > 1 ? themes[1] : null;
        String theme3 = themes.length > 2 ? themes[2] : null;
        System.out.println("[테마1] " + theme1 + ", [테마2] " + theme2 + ", [테마3] " + theme3);

// 4. FairyTale 저장
        FairyTale fairyTale = FairyTale.builder()
                .title(title)
                .content(scenes.isEmpty() ? "" : scenes.get(0))  // 첫 장면
                .type(Type.MORE)
                .background(request.getBackground())
                .theme1(theme1)
                .theme2(theme2)
                .theme3(theme3)
                .user(user)
                .isFavorite(false)
                .build();
        fairyTaleRepository.save(fairyTale);
        System.out.println("[동화 저장 완료] ID: " + fairyTale.getId());

// 5. Page 저장
        for (String scene : scenes) {

            ApiResponse<String> response = emotionInterfaceService.emotionHtml(userId, scene);
            String emotionText = response.getResult();

            Page page = Page.builder()
                    .plot(scene)
                    .fairyTale(fairyTale)
                    .emotionText(emotionText)
                    .build();
            pageRepository.save(page);
            System.out.println("[장면 저장 완료] " + scene);
        }

// 6. FairyParticipation 저장
        if (fairies != null) {
            for (Fairy fairy : fairies) {
                FairyParticipation participation = FairyParticipation.builder()
                        .fairy(fairy)
                        .fairyTale(fairyTale)
                        .build();
                fairyParticipationRepository.save(participation);
                System.out.println("[요정 참여 정보 저장 완료] 요정 ID: " + fairy.getId());
            }
        }


        // 포인트 차감
        user.setPoint(user.getPoint() - 200);
        userRepository.save(user);


        MixResponse response = MixResponse.builder()
                .fairyTaleId(fairyTale.getId())
                .title(title)
                .content(scenes.isEmpty() ? "" : scenes.get(0))  // 첫 장면
                .build();


        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, response);
    }


    // 정규식으로 단일 추출 (첫 그룹)
    private String extractByPattern(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }


    private Map<String, List<String>> extractLines(String text) {
        Map<String, List<String>> characterLines = new HashMap<>();
        Pattern pattern = Pattern.compile("-\\s*([\\uAC00-\\uD7A3\\w ]+):\\s*'([^']+)'"); // 한글 이름까지 포함
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String character = matcher.group(1).trim();
            String line = matcher.group(2).trim();
            characterLines.computeIfAbsent(character, k -> new ArrayList<>()).add(line);
        }
        return characterLines;
    }



    // "Scene 1:", "Scene 2:" ... 구간 추출
    private List<String> extractScenes(String text) {
        List<String> scenes = new ArrayList<>();
        Pattern pattern = Pattern.compile("Scene \\d+:\\s*(.*?)(?=Scene \\d+:|교훈:|등장인물 주요 대사 요약:|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String scene = matcher.group(1).trim();
            scenes.add(scene);
        }
        return scenes;
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