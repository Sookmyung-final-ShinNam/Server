package com.example.demo.extra.main.service;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.exception.CustomException;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.base.api.status.SuccessStatus;
import com.example.demo.extra.fairy.dto.FairyConverter;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.FairyRepository;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.extra.main.dto.FavoriteFairy;
import com.example.demo.extra.main.dto.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

    private final UserRepository userRepository;
    private final FairyRepository fairyRepository;

    public ApiResponse<?> getMain(String userId) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 유저가 즐겨찾기한 요정들만
        List<FavoriteFairy> fairies = FairyConverter.toFavoriteFairiesResponse(
                fairyRepository.findByUserAndIsFavorite(user, true));


        // TODO: 요정들 리스트 5개 넘으면 에러
        // 뭐야

        return ApiResponse.of(SuccessStatus.USER_INFO_RETRIEVED, UserConverter.toUserInfo(user, fairies));
    }

    public ApiResponse<?> increaseMaxFairyCount(String userId) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        user.setMaxFairyNum(user.getMaxFairyNum() + 1);
        user.setPoint(user.getPoint() - 300);
        userRepository.save(user);

        return ApiResponse.of(SuccessStatus.FAIRY_MAX_NUM_INCREASED, user.getMaxFairyNum());
    }

    public ApiResponse<?> increaseMaxStoryCount(String userId) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        user.setMaxFairyTaleNum(user.getMaxFairyTaleNum() +1);
        user.setPoint(user.getPoint() - 300);
        userRepository.save(user);

        return ApiResponse.of(SuccessStatus.FAIRY_TALE_MAX_NUM_INCREASED, user.getMaxFairyTaleNum());
    }



}