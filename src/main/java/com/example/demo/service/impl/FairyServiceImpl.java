package com.example.demo.service.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.fairy.FairyConverter;
import com.example.demo.domain.converter.fairyTale.FairyTaleConverter;
import com.example.demo.domain.dto.fairy.FairyRequest;
import com.example.demo.domain.dto.fairy.FairyResponse;
import com.example.demo.entity.base.*;
import com.example.demo.repository.*;
import com.example.demo.service.FairyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FairyServiceImpl implements FairyService {

    private final FairyConverter fairyConverter;
    private final FairyTaleConverter fairyTaleConverter;
    private final UserRepository userRepository;
    private final FairyRepository fairyRepository;
    private final FairyTaleRepository fairyTaleRepository;

    public FairyServiceImpl(FairyConverter fairyConverter,
                            FairyTaleConverter fairyTaleConverter,
                            UserRepository userRepository,
                            FairyRepository fairyRepository,
                            FairyTaleRepository fairyTaleRepository) {
        this.fairyConverter = fairyConverter;
        this.fairyTaleConverter = fairyTaleConverter;
        this.userRepository = userRepository;
        this.fairyRepository = fairyRepository;
        this.fairyTaleRepository = fairyTaleRepository;
    }

    @Override
    @Transactional
    public ApiResponse<?> createFairy(String userId, FairyRequest request) {
        // 1. 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 2. 요정 엔티티 생성 (컨버터 사용)
        Fairy fairy = fairyConverter.toEntity(request, user);

        // 3. 동화 엔티티 생성 (컨버터 사용)
        FairyTale fairyTale = fairyTaleConverter.toEntity(request, user);

        // 4. 출연 기록 생성
        FairyAppearance appearance = FairyAppearance.builder()
                .fairy(fairy)
                .fairyTale(fairyTale)
                .build();

        // 5. 관계 설정
        fairy.getAppearances().add(appearance);
        fairyTale.getAppearances().add(appearance);

        // 6. 저장
        fairyRepository.save(fairy);
        fairyTaleRepository.save(fairyTale);

        // 7. 응답 DTO 생성
        FairyResponse response = FairyResponse.builder()
                .id(fairy.getId())
                .name(fairy.getName())
                .personality(fairy.getPersonality())
                .appearance(fairy.getAppearance())
                .fairyTaleTitle(fairyTale.getTitle())
                .fairyTaleContent(fairyTale.getContent())
                .build();

        // 8. 응답 반환
        return ApiResponse.of(SuccessStatus.FAIRY_CREATED, response);
    }

    @Override
    public ApiResponse<?> getMyFairies(String userId) {
        List<Fairy> fairies = fairyRepository.findAllByUser_UserId(userId);
        return ApiResponse.of(SuccessStatus.FAIRY_LIST_RETRIEVED, fairies);
    }

}