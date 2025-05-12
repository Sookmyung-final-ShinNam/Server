package com.example.demo.service.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.FairyTale.FairyEndingRequest;
import com.example.demo.domain.dto.fairy.FairyRequest;
import com.example.demo.domain.dto.gpt.*;
import com.example.demo.entity.base.FairyTale;
import com.example.demo.repository.FairyTaleRepository;
import com.example.demo.service.ChatService;
import com.example.demo.service.FairyService;
import com.example.demo.service.FairyTaleService;
import com.example.demo.util.PromptLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
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
    @Autowired
    private FairyTaleService fairyTaleService;


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
                .personality("미정")
                .appearance(appearance)
                .title(title)
                .content(answer)
                .build();

        return fairyService.createFairy(userId, fairyRequest);
    }

    @Override
    public ApiResponse generateQuestion(String userId, StoryRequest request) {

        String promptFileName = "question_num1.json";
        switch (request.getNowTry()) {
            case "2":
                promptFileName = "question_num2.json";
                break;
            case "3":
                promptFileName = "question_num3.json";
                break;
            case "4":
                promptFileName = "question_num4.json";
                break;
        }

        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        // 동화 번호로 FairyTale 엔티티 조회
        FairyTale fairyTale = fairyTaleRepository.findById(Long.parseLong(request.getFairyTaleNum()))
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_TALE_NOT_FOUND));

        // 동화의 content를 situation으로 설정
        String body = bodyTemplate
                .replace("{situation}", fairyTale.getContent());

        // GPT 호출
        String answer = callChatGpt(body);

        fairyTaleService.updateFairyTaleContent(userId, Long.valueOf(request.getFairyTaleNum()), answer);

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);

    }

    @Override
    public ApiResponse generateNext(String userId, StoryRequest request) {

        String promptFileName = "nextStory_num1.json";
        switch (request.getNowTry()) {
            case "2":
                promptFileName = "nextStory_num2.json";
                break;
            case "3":
                promptFileName = "nextStory_num3.json";
                break;
            case "4":
                promptFileName = "nextStory_num4.json";
                break;
        }

        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        // 동화 번호로 FairyTale 엔티티 조회
        FairyTale fairyTale = fairyTaleRepository.findById(Long.parseLong(request.getFairyTaleNum()))
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_TALE_NOT_FOUND));

        // 동화의 content를 situation으로 설정
        String body = bodyTemplate
                .replace("{situation}", fairyTale.getContent());

        // GPT 호출
        String answer = callChatGpt(body);

        fairyTaleService.updateFairyTaleContent(userId, Long.valueOf(request.getFairyTaleNum()), answer);

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);

    }

    @Override
    public ApiResponse provideFeedback(String userId, FeedbackRequest request) {

        var promptFileName = "feedback_base_userAnswer.json";
        if (request.getTryNum().equals("3"))
            promptFileName = "feedback_make_next.json";
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        // 동화 번호로 FairyTale 엔티티 조회
        FairyTale fairyTale = fairyTaleRepository.findById(Long.parseLong(request.getFairyTaleNum()))
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_TALE_NOT_FOUND));

        String body = bodyTemplate
                .replace("{situation}", fairyTale.getContent())
                .replace("{user_answer}", request.getUserAnswer());

        // GPT 호출
        String answer = callChatGpt(body);

        boolean isAppropriateAnswer = false;
        String result = null;

        log.debug("최종 분석 전 결과: {}", answer);

        if (answer.startsWith("맞아")) {
            isAppropriateAnswer = true;
            result = answer.substring(4).trim();
            fairyTaleService.updateFairyTaleContent(userId, Long.valueOf(request.getFairyTaleNum()), answer);
        } else if (answer.startsWith("아니야")) {
            isAppropriateAnswer = false;
            result = answer.substring(5).trim();
        }

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, new StoryFeedbackResult(result, isAppropriateAnswer));
    }


    // 공통 부분 : gpt 호출
    private String callChatGpt(String userMessage) {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // JSON 요청 본문 구성
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", "gpt-3.5-turbo");

            ArrayNode messages = mapper.createArrayNode();
            ObjectNode userMessageNode = mapper.createObjectNode();
            userMessageNode.put("role", "user");
            userMessageNode.put("content", userMessage); // 여기서 큰따옴표 자동 이스케이프 처리됨
            messages.add(userMessageNode);

            requestBody.set("messages", messages);

            // 객체를 JSON 문자열로 직렬화
            String json = mapper.writeValueAsString(requestBody);

            // 요청 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 응답 읽기
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                JsonNode jsonNode = mapper.readTree(response.toString());
                return jsonNode.get("choices").get(0).get("message").get("content").asText();
            }

        } catch (IOException e) {
            throw new CustomException(ErrorStatus.CHAT_GPT_API_CALL_FAILED);
        }
    }


}