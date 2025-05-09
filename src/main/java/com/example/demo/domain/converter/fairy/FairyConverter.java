package com.example.demo.domain.converter.fairy;

import com.example.demo.domain.dto.fairy.FairyRequest;
import com.example.demo.entity.base.User;
import com.example.demo.entity.base.Fairy;
import com.example.demo.entity.enums.Gender;
import org.springframework.stereotype.Component;

@Component
public class FairyConverter {

    public Fairy toEntity(FairyRequest request, User user) {
        return Fairy.builder()
                .name(request.getName())
                .personality(request.getPersonality())
                .appearance(request.getAppearance())
                .age(request.getAge())
                .gender(Gender.valueOf(request.getGender().toUpperCase()))
                .user(user)
                .build();
    }

}