package com.example.demo.image.lora;

import com.example.demo.base.BaseController;
import com.example.demo.base.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/image")
public class LoraController extends BaseController {

    @Autowired
    private LoraImageGenerationService imageGenerationService;

    // 1번 기능 : 요정 이미지 & 탄생 동화 이미지
    @GetMapping("/lora")
    public ApiResponse<?> getMyFairies(LoraImageRequestDto requestDto) {
        String userId = getCurrentUserId();

        return imageGenerationService.getMyFairies(userId, requestDto);
    }


}