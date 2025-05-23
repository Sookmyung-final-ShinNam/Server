package com.example.demo.generate.dto;

import com.example.demo.domain.entity.enums.Gender;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoryEndResponse {
    private Long fairyId;
    private String fairyLine;
    private String name;
    private Integer age;
    private Gender gender;
    private String personality;
    private String imageUrl;
}