package com.example.demo.domain.converter.fairyTale;

import com.example.demo.entity.base.FairyTale;

import java.util.List;

public class ParticipantConverter {

    public static List<String> toFairyNames(FairyTale fairyTale) {
        return fairyTale.getParticipations().stream()
                .map(p -> p.getFairy().getName())
                .toList();
    }
}
