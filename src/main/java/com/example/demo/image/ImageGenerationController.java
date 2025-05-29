package com.example.demo.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageGenerationController {

    private final ImageGenerationService imageGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateImage(@RequestBody ImageRequestDto request) {
        try {
            String savedImagePath = imageGenerationService.generateImage(request);
            return ResponseEntity.ok("이미지 생성 완료: " + savedImagePath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 생성 실패: " + e.getMessage());
        }
    }
}
