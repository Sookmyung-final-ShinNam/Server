package com.example.demo.extra.fairyTale.dto;

import com.example.demo.domain.entity.FairyTale;

import java.util.List;

public class ParticipantConverter {

    public static List<String> toFairyNames(FairyTale fairyTale) {
        return fairyTale.getParticipations().stream()
                .map(p -> p.getFairy().getName())
                .toList();
    }
}
