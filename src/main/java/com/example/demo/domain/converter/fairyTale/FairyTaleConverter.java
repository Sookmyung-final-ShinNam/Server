package com.example.demo.domain.converter.fairyTale;

import com.example.demo.domain.dto.fairy.FairyRequest;
import com.example.demo.entity.base.FairyTale;
import com.example.demo.entity.base.User;
import org.springframework.stereotype.Component;

@Component
public class FairyTaleConverter {

    public FairyTale toEntity(FairyRequest request, User user) {
        return FairyTale.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();
    }

}