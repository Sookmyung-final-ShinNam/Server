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
import com.example.demo.repository.PageRepository;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final PromptLoader promptLoader;

    @Value("${chatgpt.api-key}")
    private String apiKey;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private FairyTaleRepository fairyTaleRepository;

    @Autowired
    private FairyRepository fairyRepository;


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
        String promptFileName = getPromptFileName("question_num", request.getNowTry());
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        FairyTale fairyTale = getFairyTaleOrThrow(request.getFairyTaleNum());
        String combinedContent = buildCombinedContent(fairyTale);

        String body = bodyTemplate.replace("{situation}", combinedContent);
        String answer = callChatGpt(body);

        savePageWithField(fairyTale, "question", answer);

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);
    }

    @Override
    public ApiResponse generateNext(String userId, StoryRequest request) {
        String promptFileName = getPromptFileName("nextStory_num", request.getNowTry());
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        log.info("🔎 현재 번호 : {}", request.getNowTry());

        FairyTale fairyTale = getFairyTaleOrThrow(request.getFairyTaleNum());
        String combinedContent = buildCombinedContent(fairyTale);

        String body = bodyTemplate.replace("{situation}", combinedContent);
        String answer = callChatGpt(body);

        savePageWithField(fairyTale, "plot", answer);

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, answer);
    }

    @Override
    public ApiResponse provideFeedback(String userId, FeedbackRequest request) {
        log.info("🔎 현재 번호 : {}", request.getTryNum());

        String promptFileName = request.getTryNum().equals("3")
                ? "feedback_make_next.json"
                : "feedback_base_userAnswer.json";
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        FairyTale fairyTale = getFairyTaleOrThrow(request.getFairyTaleNum());
        String combinedContent = buildCombinedContent(fairyTale);

        String body = bodyTemplate
                .replace("{situation}", combinedContent)
                .replace("{user_answer}", request.getUserAnswer());

        String answer = callChatGpt(body);

        log.debug("최종 분석 전 결과: {}", answer);

        boolean isAppropriateAnswer = false;
        String result;

        if (answer.startsWith("맞아")) {
            isAppropriateAnswer = true;
            result = answer.substring(4).trim();
            savePageWithField(fairyTale, "answer", answer);
        } else if (answer.startsWith("아니야")) {
            isAppropriateAnswer = false;
            result = answer.substring(5).trim();
        } else {
            result = answer;
        }

        return ApiResponse.of(SuccessStatus.CHAT_SUCCESS, new StoryFeedbackResult(result, isAppropriateAnswer));
    }


    // 공통 부분
    private String getPromptFileName(String baseName, String nowTry) {
        return switch (nowTry) {
            case "2" -> baseName + "2.json";
            case "3" -> baseName + "3.json";
            case "4" -> baseName + "4.json";
            default -> baseName + "1.json";
        };
    }

    private FairyTale getFairyTaleOrThrow(String fairyTaleNum) {
        return fairyTaleRepository.findById(Long.parseLong(fairyTaleNum))
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_TALE_NOT_FOUND));
    }

    private String buildCombinedContent(FairyTale fairyTale) {
        List<Page> pages = pageRepository.findByFairyTaleOrderByIdAsc(fairyTale);

        StringBuilder combinedContent = new StringBuilder();
        for (Page page : pages) {
            if (page.getQuestion() != null) {
                combinedContent.append("질문: ").append(page.getQuestion()).append("\n");
            }
            if (page.getAnswer() != null) {
                combinedContent.append("답변: ").append(page.getAnswer()).append("\n");
            }
            if (page.getPlot() != null) {
                combinedContent.append("줄거리: ").append(page.getPlot()).append("\n");
            }
        }
        return combinedContent.toString();
    }

    private void savePageWithField(FairyTale fairyTale, String field, String content) {
        Page targetPage;

        if ("question".equals(field)) {
            // question는 새로운 Page 생성
            targetPage = new Page();
            targetPage.setFairyTale(fairyTale);
        } else {
            // answer 또는 plot은 마지막 Page 수정
            Optional<Page> optionalLastPage = pageRepository.findTopByFairyTaleOrderByIdDesc(fairyTale);
            if (optionalLastPage.isEmpty()) {
                throw new CustomException(ErrorStatus.COMMON_BAD_REQUEST); // 마지막 페이지가 없으면 예외 처리
            }
            targetPage = optionalLastPage.get();
        }

        switch (field) {
            case "question" -> targetPage.setQuestion(content);
            case "answer" -> targetPage.setAnswer(content);
            case "plot" -> targetPage.setPlot(content);
            default -> throw new CustomException(ErrorStatus.COMMON_BAD_REQUEST);
        }

        pageRepository.save(targetPage);
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

            // ✅ 응답 받기
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                JsonNode responseJson = mapper.readTree(response.toString());
                return responseJson.get("choices").get(0).get("message").get("content").asText();
            }

        } catch (IOException e) {
            throw new CustomException(ErrorStatus.CHAT_GPT_API_CALL_FAILED);
        }
    }

}