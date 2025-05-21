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
    public ApiResponse<?> getMyFairyTales(String email) {
        List<FairyTale> fairyTales = fairyTaleRepository.findAllByUserEmail(email);
        return ApiResponse.of(SuccessStatus.FAIRY_TALE_LIST_RETRIEVED, fairyTales);
    }

    @Override
    public ApiResponse<?> getFairyTaleContent(String email, Long fairyTaleId) {
        FairyTale fairyTale = fairyTaleRepository.findByIdAndUserEmail(fairyTaleId, email)
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_TALE_NOT_FOUND));

        return ApiResponse.of(SuccessStatus.FAIRY_TALE_CONTENT_RETRIEVED, fairyTale.getContent());
    }

}