package com.example.demo.image;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.base.api.status.SuccessStatus;
import com.example.demo.domain.entity.Fairy;
import com.example.demo.domain.entity.FairyTale;
import com.example.demo.domain.entity.Page;
import com.example.demo.domain.repository.FairyRepository;
import com.example.demo.domain.repository.FairyTaleRepository;
import com.example.demo.domain.repository.PageRepository;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.image.s3.FileDTO;
import com.example.demo.image.s3.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageGenerationService {

    private final RestTemplate restTemplate;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final FairyRepository fairyRepository;
    private final FairyTaleRepository fairyTaleRepository;
    private final PageRepository pageRepository;

    // 외형 + 스타일 고정
    private static final String BASE_PROMPT = "a wholesome, child-safe, kindergarten-aged cartoon character, "
            + "in a long-sleeved pastel clothes, wearing tights and shoes, "
            + "friendly and cute, colorful fairytale style, full body, "
            + "Studio Ghibli style, white background";

    // 부적절하거나 품질이 낮은 요소 제거
    private static final String NEGATIVE_PROMPT = String.join(", ",
            "nsfw", "nude", "naked", "lingerie", "swimsuit", "cleavage", "breasts",
            "exposed skin", "revealing outfit", "tight clothes", "suggestive pose", "sexualized",
            "erotic", "lewd", "short skirt", "open shirt", "inappropriate", "unrealistic proportions",
            "bad anatomy", "poorly drawn hands", "extra limbs", "blurry", "low quality", "scary",
            "creepy", "dark shadows");

    public ApiResponse<?> getMyFairies(String userId, ImageRequestDto dto) {

        System.out.println("🟡 이미지 생성 시작");

        Fairy fairy = fairyRepository.findById(dto.getFairyId())
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_NOT_FOUND));

        userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        FairyTale fairyTale = fairyTaleRepository.findById(dto.getFairyTaleId())
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_TALE_NOT_FOUND));

        List<Page> pages = pageRepository.findByFairyTale(fairyTale);

        List<String> plots = pages.stream()
                .map(Page::getPlot)
                .filter(Objects::nonNull)
                .toList();

        List<String> uploadedImageUrls = new ArrayList<>();

        // 기본 동작 (첫 번째 기본 이미지)
        List<String> behaviors = new ArrayList<>();
        behaviors.add("The girl stands smiling brightly with one hand waving in front of a white background.");

        // 나머지는 줄거리를 가져옴.
        behaviors.addAll(plots);

        for (String behavior : behaviors) {
            // 프롬프트 구성
            String fullPrompt = BASE_PROMPT + ", " + fairy.getAppearance() + ", " + behavior;
            System.out.println("📌 프롬프트 구성 완료: " + fullPrompt);

            Map<String, Object> payload = new HashMap<>();
            payload.put("prompt", fullPrompt);
            payload.put("negative_prompt", NEGATIVE_PROMPT);
            payload.put("steps", 30);
            payload.put("sampler_name", "DPM++ 2M");
            payload.put("scheduler", "Karras");
            payload.put("cfg_scale", 7);
            payload.put("width", 540);
            payload.put("height", 540);

            // ✅ 외형 고정: seed 고정
            payload.put("seed", 123456789L);
            payload.put("enable_hr", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            String url = "https://55623e0fc02f.ngrok.app/sdapi/v1/txt2img";
            System.out.println("🔁 API 요청 전송 중...");
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            System.out.println("✅ 응답 수신 완료");

            List<String> images = (List<String>) response.getBody().get("images");
            if (images == null || images.isEmpty()) {
                System.out.println("❌ 이미지 생성 실패");
                continue;
            }

            String base64Image = images.get(0).split(",").length > 1 ?
                    images.get(0).split(",")[1] : images.get(0);

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            MultipartFile multipartFile = new MockMultipartFile(
                    "file",
                    UUID.randomUUID() + ".png",
                    MediaType.IMAGE_PNG_VALUE,
                    imageBytes
            );

            ApiResponse<FileDTO> uploadResponse = fileService.uploadFile(
                    "characters",
                    multipartFile.getOriginalFilename(),
                    multipartFile
            );

            uploadedImageUrls.add(uploadResponse.getResult().getS3Url());
        }

        // 첫 이미지는 기본 프로필 이미지로 저장
        if (!uploadedImageUrls.isEmpty()) {
            fairy.setFirstImage(uploadedImageUrls.get(0));
            fairyRepository.save(fairy);
        }

        // 나머지 이미지를 페이지에 매핑
        if (uploadedImageUrls.size() > 1) {
            for (int i = 1; i < uploadedImageUrls.size(); i++) {
                if (i - 1 < pages.size()) {
                    Page page = pages.get(i - 1);
                    page.setImage(uploadedImageUrls.get(i));
                }
            }
            pageRepository.saveAll(pages);
        }

        return ApiResponse.of(SuccessStatus._OK, uploadedImageUrls);
    }



}