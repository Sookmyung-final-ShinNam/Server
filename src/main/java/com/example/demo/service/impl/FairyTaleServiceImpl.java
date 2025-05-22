package com.example.demo.service.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.fairy.FairyInfoResponse2;
import com.example.demo.domain.dto.fairyTale.FairyTaleResponse;
import com.example.demo.entity.base.FairyTale;
import com.example.demo.entity.base.User;
import com.example.demo.entity.enums.Gender;
import com.example.demo.entity.enums.Type;
import com.example.demo.repository.FairyTaleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FairyTaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FairyTaleServiceImpl implements FairyTaleService {

    private final FairyTaleRepository fairyTaleRepository;
    private final UserRepository userRepository;

    // 사용자 동화 목록 조회 (타입)
    @Override
    public ApiResponse<?> getMyFairyTalesWithType(String email, String type) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        List<FairyTaleResponse> fairyTales;
        if (type.equalsIgnoreCase("all")) {
            // 전체 타입 조회
            fairyTales = fairyTaleRepository.findAllByUser(user);
        } else {
            try {
                // ONE, MORE인 경우
                Type selctedType = Type.valueOf(type.toUpperCase());
                fairyTales = fairyTaleRepository.findAllByUserAndType(user, selctedType);
            } catch (IllegalArgumentException e) {
                // 그 외 입력한 경우
                return ApiResponse.onFailure(ErrorStatus.FAIRY_TALE_INVALID_TYPE, null);
            }
        }

        return ApiResponse.of(SuccessStatus.FAIRY_LIST_RETRIEVED, fairyTales);
    }

    // 사용자 요정 목록 조회 (즐겨찾기)
    @Override
    public ApiResponse<?> getMyFairyTalesWithFavorite(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        List<FairyTaleResponse> fairyTales = fairyTaleRepository.findAllByUserAndIsFavorite(user, true);
        return ApiResponse.of(SuccessStatus.FAIRY_TALE_LIST_RETRIEVED, fairyTales);
    }

    @Override
    public ApiResponse<?> getFairyTaleContent(String email, Long fairyTaleId) {
        FairyTale fairyTale = fairyTaleRepository.findByIdAndUserEmail(fairyTaleId, email)
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_TALE_NOT_FOUND));

        return ApiResponse.of(SuccessStatus.FAIRY_TALE_CONTENT_RETRIEVED, fairyTale.getContent());
    }

}