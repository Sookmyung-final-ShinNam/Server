package com.example.demo.extra.fairy.service;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.base.api.status.SuccessStatus;
import com.example.demo.domain.repository.FairyRepository;
import com.example.demo.extra.fairy.dto.FairyConverter;
import com.example.demo.extra.fairy.dto.FairyInfoResponse2;
import com.example.demo.extra.fairy.dto.MyFairyResponse;
import com.example.demo.extra.fairyTale.dto.FairyTaleConverter;
import com.example.demo.domain.repository.FairyTaleRepository;
import com.example.demo.domain.entity.Fairy;
import com.example.demo.domain.entity.enums.Gender;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FairyServiceImpl implements FairyService {

    private final FairyConverter fairyConverter;
    private final UserRepository userRepository;
    private final FairyRepository fairyRepository;

    public FairyServiceImpl(FairyConverter fairyConverter,
                            FairyTaleConverter fairyTaleConverter,
                            UserRepository userRepository,
                            FairyRepository fairyRepository,
                            FairyTaleRepository fairyTaleRepository) {
        this.fairyConverter = fairyConverter;
        this.userRepository = userRepository;
        this.fairyRepository = fairyRepository;
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
    @Transactional
    public ApiResponse<?> updateFavoriteStatus(String email, Long fairyId) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Fairy fairy = fairyRepository.findById(fairyId)
                .orElseThrow(() -> new CustomException(ErrorStatus.FAIRY_NOT_FOUND));

        fairy.updateFavoriteStatus(!fairy.getIsFavorite());
        fairyRepository.save(fairy);

        return ApiResponse.of(SuccessStatus.FAIRY_UPDATED,
                fairyId + " 요정의 수정된 favorite 상태 - " + fairy.getIsFavorite());
    }
}