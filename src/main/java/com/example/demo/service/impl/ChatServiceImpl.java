package com.example.demo.service.impl;

import org.json.JSONObject;
import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;

import com.example.demo.domain.dto.gpt.*;
import com.example.demo.entity.base.*;
import com.example.demo.entity.enums.Gender;
import com.example.demo.entity.enums.Type;
import com.example.demo.repository.*;
import com.example.demo.service.ChatService;
import com.example.demo.util.PromptLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private PageDraftRepository pageDraftRepository;

    @Autowired
    private PageRepository pageRepository;


    @Autowired
    private FairyParticipationRepository fairyParticipationRepository;


    @Autowired
    private FairyLineRepository fairyLineRepository;

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

        // 5. PageDraft 생성 (answer 내용을 저장)
        PageDraft pageDraft = PageDraft.builder()
                .next(answer)
                .fairyTale(fairyTale)
                .build();

        // FairyTale에 페이지Draft 추가
        fairyTale.getPageDrafts().add(pageDraft);

        // 6. DB 저장 (Repository 호출)
        fairyTaleRepository.save(fairyTale);

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
    @Transactional
    public ApiResponse generateNext(String userId, StoryRequest request) {
        String promptFileName = getPromptFileName("nextStory_num", request.getNowTry());
        String bodyTemplate = promptLoader.loadPrompt(promptFileName);

        log.info("🔎 현재 번호 : {}", request.getNowTry());

        FairyTale fairyTale = getFairyTaleOrThrow(request.getFairyTaleNum());
        String combinedContent = buildCombinedContent(fairyTale);

        String body = bodyTemplate.replace("{situation}", combinedContent);
        String answer = callChatGpt(body);

        savePageWithField(fairyTale, "next", answer);


        log.info("📚 답변 성공 : {}", answer);

        combinedContent = buildPromptFromPageDraft(fairyTale);

        // nowTry 가 4일 때 summaryStory를 호출하고 저장된 모든 칸을 삭제
        if ("4".equals(request.getNowTry())) {
            // 1. 전체 줄거리 출력
            log.info("📚 전체 줄거리 완성: {}", combinedContent);

            // 2. 지금까지 쌓인 모든 페이지 삭제
            User user = userRepository.findByEmail(userId)
                    .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

            pageDraftRepository.deleteByFairyTaleIdAndFairyTaleUserId(fairyTale.getId(), user.getId());
            log.info("🗑️ 지금까지 쌓인 모든 페이지를 삭제했습니다.");


            bodyTemplate = promptLoader.loadPrompt("summary_story.json");


            body = bodyTemplate.replace("{situation}", combinedContent);
            answer = callChatGpt(body);
            log.info("answer: {}", answer);



            // 1. 제목 추출 및 저장
            Pattern titlePattern = Pattern.compile("^(.+?)\\n\\n장면 1:", Pattern.DOTALL);
            Matcher titleMatcher = titlePattern.matcher(answer);
            if (titleMatcher.find()) {
                String title = titleMatcher.group(1).trim();
                fairyTale.setTitle(title);
            }

            // 2. 장면 추출 및 Page 저장
            Pattern scenePattern = Pattern.compile("장면 \\d+:(.*?)(?=장면 \\d+:|주인공 성격 *:|\\w+의 대사 배열|$)", Pattern.DOTALL);
            Matcher sceneMatcher = scenePattern.matcher(answer);

            List<Page> pages = new ArrayList<>();
            String firstSceneContent = null;
            int index = 0;

            while (sceneMatcher.find()) {
                String plot = sceneMatcher.group(1).trim();
                if (!plot.isEmpty()) {
                    Page page = Page.builder()
                            .plot(plot)
                            .fairyTale(fairyTale)
                            .build();
                    pages.add(page);

                    if (index == 0) {
                        firstSceneContent = plot;
                    }
                    index++;
                }
            }

            // 3. 첫 번째 장면 저장
            if (firstSceneContent != null) {
                fairyTale.setContent(firstSceneContent);
            } else {
                log.warn("firstSceneContent 추출 실패");
            }

            // 4. 요정 조회
            Optional<FairyParticipation> participationOpt = fairyParticipationRepository.findFirstByFairyTale(fairyTale);
            Fairy fairy = null;
            if (participationOpt.isPresent()) {
                fairy = participationOpt.get().getFairy();
            } else {
                log.warn("요정 조회 실패: 해당 동화에 출연한 요정을 찾을 수 없습니다. fairyTale ID = {}", fairyTale.getId());
            }

            // 5. 성격 추출 및 저장
            Pattern personalityPattern = Pattern.compile("주인공 성격 *: *(.+?)\\n");
            Matcher personalityMatcher = personalityPattern.matcher(answer);
            if (personalityMatcher.find() && fairy != null) {
                String personality = personalityMatcher.group(1).trim();
                fairy.setPersonality(personality);
            }

            // 6. 대사 추출 및 저장 (요정 이름 유연하게 대응)
            Pattern linePattern = Pattern.compile("([^\\s]+)의 대사 배열\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
            Matcher lineMatcher = linePattern.matcher(answer);
            List<FairyLine> lines = new ArrayList<>();

            if (lineMatcher.find() && fairy != null) {
                String rawLines = lineMatcher.group(2);
                Matcher quoteMatcher = Pattern.compile("'(.*?)'|\"(.*?)\"").matcher(rawLines);

                while (quoteMatcher.find()) {
                    String line = quoteMatcher.group(1) != null ? quoteMatcher.group(1) : quoteMatcher.group(2);
                    line = line.trim();
                    if (!line.isEmpty()) {
                        FairyLine fairyLine = FairyLine.builder()
                                .line(line)
                                .fairy(fairy)
                                .build();
                        lines.add(fairyLine);
                    }
                }
            }

// 7. 저장 (순서 주의)
            pageRepository.saveAll(pages);
            fairyTaleRepository.save(fairyTale);
            if (fairy != null) {
                fairyRepository.save(fairy);
                fairyLineRepository.saveAll(lines);
            }

// 8. 디버깅 로그
            log.info("총 저장된 장면 수: {}", pages.size());
            for (Page p : pages) {
                log.debug("장면 내용: {}", p.getPlot());
            }
            log.info("저장된 대사 수: {}", lines.size());
            for (FairyLine fl : lines) {
                log.debug("대사: {}", fl.getLine());
            }


        }

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
        List<PageDraft> pageDrafts = pageDraftRepository.findByFairyTaleOrderByIdAsc(fairyTale);

        StringBuilder combinedContent = new StringBuilder();
        for (PageDraft pageDraft : pageDrafts) {
            if (pageDraft.getQuestion() != null) {
                combinedContent.append("질문: ").append(pageDraft.getQuestion()).append("\n");
            }
            if (pageDraft.getAnswer() != null) {
                combinedContent.append("답변: ").append(pageDraft.getAnswer()).append("\n");
            }
            if (pageDraft.getNext() != null) {
                combinedContent.append("다음이야기: ").append(pageDraft.getNext()).append("\n");
            }
        }
        return combinedContent.toString();
    }

    private String buildPromptFromPageDraft(FairyTale fairyTale) {
        List<PageDraft> drafts = pageDraftRepository.findByFairyTaleOrderByIdAsc(fairyTale);

        StringBuilder sb = new StringBuilder();
        int chapter = 1;
        for (PageDraft draft : drafts) {
            sb.append("### 장면 ").append(chapter).append(" ###\n");
            if (draft.getQuestion() != null && !draft.getQuestion().isBlank()) {
                sb.append("질문: ").append(draft.getQuestion()).append("\n");
            }
            if (draft.getAnswer() != null && !draft.getAnswer().isBlank()) {
                sb.append("답변: ").append(draft.getAnswer()).append("\n");
            }
            if (draft.getNext() != null && !draft.getNext().isBlank()) {
                sb.append("다음 이야기: ").append(draft.getNext()).append("\n");
            }
            sb.append("\n");
            chapter++;
        }
        return sb.toString();
    }


    private void savePageWithField(FairyTale fairyTale, String field, String content) {
        PageDraft targetPage;

        if ("question".equals(field)) {
            // question는 새로운 Page 생성
            targetPage = new PageDraft();
            targetPage.setFairyTale(fairyTale);
        } else {
            // answer 또는 next은 마지막 칸 수정
            Optional<PageDraft> optionalLastPage = pageDraftRepository.findTopByFairyTaleOrderByIdDesc(fairyTale);
            if (optionalLastPage.isEmpty()) {
                throw new CustomException(ErrorStatus.COMMON_BAD_REQUEST); // 마지막 페이지가 없으면 예외 처리
            }
            targetPage = optionalLastPage.get();
        }

        switch (field) {
            case "question" -> targetPage.setQuestion(content);
            case "answer" -> targetPage.setAnswer(content);
            case "next" -> targetPage.setNext(content);
            default -> throw new CustomException(ErrorStatus.COMMON_BAD_REQUEST);
        }

        pageDraftRepository.save(targetPage);
    }


    // 공통 부분 : gpt 호출
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