package com.example.demo.service.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.fairy.FairyConverter;
import com.example.demo.domain.converter.fairyTale.FairyTaleConverter;
import com.example.demo.domain.dto.fairy.*;
import com.example.demo.entity.base.*;
import com.example.demo.entity.enums.Gender;
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
    public ApiResponse createFairyInfo(String email, FairyInfoRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // TODO: 유저 보유가능한 요정수 인지 체크
        Fairy fairy = fairyConverter.toEntity(request, user);
        fairy = fairyRepository.save(fairy);

        return ApiResponse.of(SuccessStatus.FAIRY_CREATED, fairy);
    }


    // 캐릭터 섞어서 동화 생성
    @Override
    @Transactional
    public ApiResponse createFairyMix(String email, FairyMixRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // TODO: id로부터 요정정보 넘겨줌
        // TODO: 동화책만들어지면서 매핑
        return ApiResponse.of(SuccessStatus.FAIRY_CREATED, null);
    }

    // 사용자 요정 조회
    @Override
    public ApiResponse<?> getMyFairy(String email, Long fairyId) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Fairy fairy = fairyRepository.findById(fairyId)
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_NOT_FOUND));

        MyFairyResponse response = FairyConverter.toMyFairyResponse(fairy);

        return ApiResponse.of(SuccessStatus.FAIRY_RETRIEVED, response);
    }

    // 사용자 요정 목록 조회 (성별)
    @Override
    public ApiResponse<?> getMyFairiesWithGender(String email, String gender) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        List<FairyInfoResponse2> fairies;
        if (gender.equalsIgnoreCase("all")) {
            // 전체 성별 조회
            fairies = fairyRepository.findAllByUserEmail(email);
        } else {
            try {
                // MALE, FEMALE인 경우
                Gender selctedGender = Gender.valueOf(gender.toUpperCase());
                fairies = fairyRepository.findAllByUserEmailAndGender(email, selctedGender);

            } catch (IllegalArgumentException e) {
                // 그 외 입력한 경우
                return ApiResponse.onFailure(ErrorStatus.FAIRY_INVALID_GENDER, null);
            }
        }

        return ApiResponse.of(SuccessStatus.FAIRY_LIST_RETRIEVED, fairies);
    }

    // 사용자 요정 목록 조회 (즐겨찾기)
    @Override
    public ApiResponse<?> getMyFairiesWithFavorite(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        List<Fairy> fairies = fairyRepository.findByUserAndIsFavorite(user, true);
        return ApiResponse.of(SuccessStatus.FAIRY_LIST_RETRIEVED, FairyConverter.toFairyInfosResponse(fairies));
    }

    // 요정 즐겨찾기 on/off
    @Override
    public ApiResponse<?> updateFavoriteStatus(String email, Long fairyId) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Fairy fairy = fairyRepository.findById(fairyId)
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_NOT_FOUND));

//        fairy.getIsFavorite()
        return null;
    }
}