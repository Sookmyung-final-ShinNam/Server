package com.example.demo.image;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        String fullPrompt = BASE_PROMPT + ", " + dto.getAppearance() + ", " + dto.getBehavior();

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

        String url = "https://4829a66bfe41.ngrok.app/sdapi/v1/txt2img";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        List<String> images = (List<String>) response.getBody().get("images");

        if (images == null || images.isEmpty()) throw new RuntimeException("이미지 생성 실패");

        String base64Image = images.get(0).split(",").length > 1 ?
                images.get(0).split(",")[1] : images.get(0);

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        String outputPath = "generated_child_character.png";
        Path path = Paths.get(outputPath);
        Files.write(path, imageBytes);

        return outputPath;
    }
}
