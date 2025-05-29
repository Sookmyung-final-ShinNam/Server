package com.example.demo.image;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.status.SuccessStatus;
import com.example.demo.image.s3.FileConverter;
import com.example.demo.image.s3.FileDTO;
import com.example.demo.image.s3.FileRepository;
import com.example.demo.image.s3.FileService;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageGenerationService {

    private final RestTemplate restTemplate;

    private final FileService fileService;
    private final FileRepository fileRepository;
    private final FileConverter fileConverter;

    private static final String BASE_PROMPT = "a wholesome, child-safe, kindergarten-aged cartoon character, "
            + "in a long-sleeved pastel clothes, wearing tights and shoes, "
            + "friendly and cute, colorful fairytale style, full body, "
            + "Studio Ghibli style, White background";

    private static final String NEGATIVE_PROMPT = String.join(", ",
            "nsfw", "nude", "naked", "lingerie", "swimsuit", "cleavage", "breasts",
            "exposed skin", "revealing outfit", "tight clothes", "suggestive pose", "sexualized",
            "erotic", "lewd", "short skirt", "open shirt", "inappropriate", "unrealistic proportions",
            "bad anatomy", "poorly drawn hands", "extra limbs", "blurry", "low quality", "scary",
            "creepy", "dark shadows");

    public String generateImage(ImageRequestDto dto) throws IOException, java.io.IOException {
        System.out.println("🟡 이미지 생성 시작");

        String fullPrompt = BASE_PROMPT + ", " + dto.getAppearance() + ", " + dto.getBehavior();
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
        payload.put("seed", 2873609975L);
        payload.put("enable_hr", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String url = "https://5d695e7ae998.ngrok.app/sdapi/v1/txt2img";
        System.out.println("🔁 API 요청 전송 중...");

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        System.out.println("✅ 응답 수신 완료");

        List<String> images = (List<String>) response.getBody().get("images");

        if (images == null || images.isEmpty()) {
            System.out.println("❌ 이미지 생성 실패: 응답 내 이미지 없음");
            throw new RuntimeException("이미지 생성 실패");
        }

        System.out.println("🖼️ 이미지 base64 수신 성공");

        String base64Image = images.get(0).split(",").length > 1 ?
                images.get(0).split(",")[1] : images.get(0);

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        // MultipartFile로 변환
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "generated_child_character.png",
                MediaType.IMAGE_PNG_VALUE,
                imageBytes
        );

        // S3에 업로드
        ApiResponse<FileDTO> uploadResponse = fileService.uploadFile("characters", "generated_child_character.png", multipartFile);

        // 업로드된 이미지 경로 반환 (예: S3 URL 또는 저장된 fileDTO 정보)
        return uploadResponse.getResult().getS3Url();  // 예: fileUrl 필드가 있는 경우
    }
}