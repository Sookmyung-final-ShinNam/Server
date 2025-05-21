package com.example.demo.service.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.gpt.*;
import com.example.demo.entity.base.*;
import com.example.demo.entity.enums.Gender;
import com.example.demo.entity.enums.Type;
import com.example.demo.repository.FairyRepository;
import com.example.demo.repository.FairyTaleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatService;
import com.example.demo.service.FairyService;
import com.example.demo.service.FairyTaleService;
import com.example.demo.util.PromptLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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
    private UserRepository userRepository;

    @Autowired
    private FairyTaleRepository fairyTaleRepository;

    @Autowired
    private FairyRepository fairyRepository;

    @Autowired
    private FairyTaleService fairyTaleService;


    @Override
    public ApiResponse generateStoryIntro(String userId, StoryIntroRequest request, String promptFileName) {
        // 1. GPT 프롬프트 생성 및 호출
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        String userSetting = String.format(
                "**사용자 설정 : 테마: %s, 배경: %s, 주인공: 이름=%s, 성별=%s, 나이=%d세, 헤어컬러=%s, 눈 색=%s, 헤어스타일=%s**",
                request.getThemes(),
                request.getBackgrounds(),
                request.getName(),
                request.getGender(),
                request.getAge(),
                request.getHairColor(),
                request.getEyeColor(),
                request.getHairStyle()
        );

        String body = bodyTemplate.replace("{guiSetting}", userSetting);

        String answer = callChatGpt(body);

        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 2. FairyTale 엔티티 생성
        // 주제는 띄어쓰기로 분리하여 theme1, theme2, theme3 에 저장
        String[] themes = request.getThemes().split("\\s+");
        String theme1 = themes.length > 0 ? themes[0] : null;
        String theme2 = themes.length > 1 ? themes[1] : null;
        String theme3 = themes.length > 2 ? themes[2] : null;

        FairyTale fairyTale = FairyTale.builder()
                .background(request.getBackgrounds())
                .type(Type.ONE)  // fairyTale.type 은 one 으로 고정
                .theme1(theme1)
                .theme2(theme2)
                .theme3(theme3)
                .user(user)
                .build();

        // 3. Fairy 엔티티 생성 및 저장
        // appearance는 머리색상, 눈 색상, 머리스타일 띄어쓰기로 연결
        String appearance = String.format("%s %s %s", request.getHairColor(), request.getEyeColor(), request.getHairStyle());

        // Gender enum 변환 예시 (대문자 변환 필요할 수 있음)
        Gender gender = Gender.valueOf(request.getGender().toUpperCase());

        Fairy fairy = Fairy.builder()
                .name(request.getName())
                .age(request.getAge())
                .gender(gender)
                .appearance(appearance)
                .user(user)
                .build();

        fairyRepository.save(fairy);

        // 4. FairyParticipation 생성 및 양방향 연관관계 설정
        FairyParticipation participation = FairyParticipation.builder()
                .fairy(fairy)
                .fairyTale(fairyTale)
                .build();

        // Fairy에 참여 기록 추가
        fairy.getParticipations().add(participation);

        // FairyTale에 참여 기록 추가
        fairyTale.getParticipations().add(participation);

        // 5. Page 생성 (answer 내용을 plot에 저장)
        Page page = Page.builder()
                .plot(answer)
                .fairyTale(fairyTale)
                .build();

        // FairyTale에 페이지 추가
        fairyTale.getPages().add(page);

        // 6. DB 저장 (Repository 호출)
        fairyTaleRepository.save(fairyTale);
        // participation과 page는 cascade 옵션에 의해 저장됨

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);
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

        // 🔎 프롬프트 로그 출력
        System.out.println("🔎 현재 번호 :\n" + request.getNowTry());

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

        // 🔎 프롬프트 로그 출력
        System.out.println("🔎 현재 번호 :\n" + request.getTryNum());

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
    private String callChatGpt(String finalPromptJson) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 🔎 프롬프트 로그 출력
            System.out.println("🔎 전달된 프롬프트(JSON):\n" + finalPromptJson);

            // JSON 문자열 유효성 체크
            ObjectMapper mapper = new ObjectMapper();
            JsonNode validatedJson;
            try {
                validatedJson = mapper.readTree(finalPromptJson);
            } catch (JsonProcessingException e) {
                System.err.println("JSON 파싱 오류 발생: " + e.getMessage());
                e.printStackTrace();
                throw new CustomException(ErrorStatus.COMMON_BAD_REQUEST);
            }

            String safeJson = mapper.writeValueAsString(validatedJson);
            System.out.println("🔎 안전하게 변환된 JSON:\n" + safeJson);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(safeJson.getBytes(StandardCharsets.UTF_8));
            }

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line.trim());
                    }
                    System.err.println("GPT 호출 실패 응답: " + errorResponse);
                }
                throw new CustomException(ErrorStatus.CHAT_GPT_API_CALL_FAILED);
            }

            // 정상 응답 처리
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                JsonNode jsonNode = mapper.readTree(response.toString());
                JsonNode choicesNode = jsonNode.get("choices");
                if (choicesNode == null || !choicesNode.isArray() || choicesNode.size() == 0) {
                    throw new CustomException(ErrorStatus.CHAT_GPT_API_CALL_FAILED);
                }

                return choicesNode.get(0).get("message").get("content").asText();
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ErrorStatus.CHAT_GPT_API_CALL_FAILED);
        } finally {
            if (conn != null) {
                conn.disconnect(); // 리소스 정리
            }
        }
    }


}