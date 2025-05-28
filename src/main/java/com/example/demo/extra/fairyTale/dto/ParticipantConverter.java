package com.example.demo.extra.fairyTale.dto;

import com.example.demo.domain.entity.FairyTale;

import java.util.List;
import java.util.stream.Collectors;

public class ParticipantConverter {

    public static List<ParticipantResponse> toFairyNames(FairyTale fairyTale) {
        return fairyTale.getParticipations().stream()
                .map(p -> new ParticipantResponse(
                        p.getFairy().getName(),
                        p.getFairy().getFirstImage()
                ))
                .collect(Collectors.toList());
    }
}