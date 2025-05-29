package com.example.demo.image.delta;

import com.example.demo.base.BaseController;
import com.example.demo.base.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/image")
public class DeltaController extends BaseController {

    @Autowired
    private DeltaImageGenerationService imageGenerationService;

    // 2번 기능 : mix 동화 생성
    @GetMapping("/mix")
    public ApiResponse<?> MixFairyTale(DeltaImageRequestDto requestDto) {
        String userId = getCurrentUserId();

        return imageGenerationService.MixFairyTale(userId, requestDto);
    }


}