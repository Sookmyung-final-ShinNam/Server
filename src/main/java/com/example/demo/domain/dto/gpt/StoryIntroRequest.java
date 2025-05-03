package com.example.demo.domain.dto.gpt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoryIntroRequest {
    private String themes;
    private String backgrounds;
    private String name;
    private String gender;
    private int age;
    private String hairColor;
    private String eyeColor;
    private String hairStyle;
}