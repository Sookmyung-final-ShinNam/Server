package com.example.demo.service;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.fairy.FairyConverter;
import com.example.demo.domain.converter.user.UserConverter;
import com.example.demo.domain.dto.user.FavoriteFairy;
import com.example.demo.domain.dto.user.UserInfo;
import com.example.demo.entity.base.Fairy;
import com.example.demo.entity.base.User;
import com.example.demo.repository.FairyRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
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


        return ApiResponse.of(SuccessStatus.USER_INFO_RETRIEVED, UserConverter.toUserInfo(user, fairies));
    }
}
