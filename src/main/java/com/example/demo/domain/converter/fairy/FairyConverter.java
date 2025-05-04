package com.example.demo.domain.converter.fairy;

import com.example.demo.domain.dto.fairy.FairyRequest;
import com.example.demo.entity.base.User;
import com.example.demo.entity.base.Fairy;
import org.springframework.stereotype.Component;

@Component
public class FairyConverter {

    public Fairy toEntity(FairyRequest request, User user) {
        return Fairy.builder()
                .name(request.getName())
                .personality(request.getPersonality())
                .appearance(request.getAppearance())
                .user(user)
                .build();
    }

}