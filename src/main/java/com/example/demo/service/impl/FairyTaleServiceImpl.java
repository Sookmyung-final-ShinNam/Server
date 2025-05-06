package com.example.demo.service.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.base.FairyTale;
import com.example.demo.repository.FairyTaleRepository;
import com.example.demo.service.FairyTaleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FairyTaleServiceImpl implements FairyTaleService {

    private final FairyTaleRepository fairyTaleRepository;

    public FairyTaleServiceImpl(FairyTaleRepository fairyTaleRepository) {
        this.fairyTaleRepository = fairyTaleRepository;
    }

    @Override
    public ApiResponse<?> getMyFairyTales(String userId) {
        List<FairyTale> fairyTales = fairyTaleRepository.findAllByUser_UserId(userId);
        return ApiResponse.of(SuccessStatus.FAIRY_TALE_LIST_RETRIEVED, fairyTales);
    }

    @Override
    public ApiResponse<?> updateFairyTaleContent(String userId, Long fairyTaleId, String content) {
        FairyTale fairyTale = fairyTaleRepository.findByIdAndUser_UserId(fairyTaleId, userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_TALE_NOT_FOUND));

        // 기존 내용에 추가
        fairyTale.setContent(fairyTale.getContent() + content); // 기존 내용 뒤에 새 내용 추가

        fairyTaleRepository.save(fairyTale);

        return ApiResponse.of(SuccessStatus.FAIRY_TALE_UPDATED, fairyTale);
    }

    @Override
    public ApiResponse<?> getFairyTaleContent(String userId, Long fairyTaleId) {
        FairyTale fairyTale = fairyTaleRepository.findByIdAndUser_UserId(fairyTaleId, userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_TALE_NOT_FOUND));

        return ApiResponse.of(SuccessStatus.FAIRY_TALE_CONTENT_RETRIEVED, fairyTale.getContent());
    }

}