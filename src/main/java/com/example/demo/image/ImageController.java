package com.example.demo.image;

import com.example.demo.base.BaseController;
import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/image")
public class ImageController extends BaseController {

    @Autowired
    private ImageGenerationService imageGenerationService;

    // 1번 기능 : 요정 이미지 & 탄생 동화 이미지
    @GetMapping("/fairy")
    public ApiResponse<?> getMyFairies(ImageRequestDto requestDto) {
        String userId = getCurrentUserId();

        return imageGenerationService.getMyFairies(userId, requestDto);
    }


}