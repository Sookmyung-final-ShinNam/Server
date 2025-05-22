package com.example.demo.extra.fairyTale.service;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.domain.entity.enums.Type;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.domain.repository.FairyTaleRepository;
import com.example.demo.base.api.status.SuccessStatus;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.entity.FairyTale;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.extra.fairyTale.dto.FairyTaleConverter;
import com.example.demo.extra.fairyTale.dto.FairyTaleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ApiResponse<?> getMyFairyTale(String email, Long fairyTaleId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        FairyTale fairyTale = fairyTaleRepository.findById(fairyTaleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_TALE_NOT_FOUND));

        if (user != fairyTale.getUser()) return ApiResponse.onFailure(ErrorStatus.FAIRY_TALE_NOT_FOUND, "해당 사용자의 동화가 아닙니다.");

        return ApiResponse.of(SuccessStatus.FAIRY_TALE_CONTENT_RETRIEVED, FairyTaleConverter.toMyFairyTaleResponse(fairyTale));
    }

    @Override
    @Transactional
    public ApiResponse<?> updateFavoriteStatus(String email, Long fairyTaleId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        FairyTale fairyTale = fairyTaleRepository.findById(fairyTaleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_NOT_FOUND));

        if (user != fairyTale.getUser()) return ApiResponse.onFailure(ErrorStatus.FAIRY_TALE_NOT_FOUND, "해당 사용자의 동화가 아닙니다.");

        fairyTale.updateFavoriteStatus(!fairyTale.getIsFavorite());
        fairyTaleRepository.save(fairyTale);

        return ApiResponse.of(SuccessStatus.FAIRY_TALE_UPDATED,
                fairyTaleId + " 동화의 수정된 favorite 상태 - " + fairyTale.getIsFavorite());
    }
}