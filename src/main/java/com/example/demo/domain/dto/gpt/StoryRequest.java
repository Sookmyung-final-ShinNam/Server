package com.example.demo.domain.dto.gpt;

import lombok.Getter;

@Getter
public class StoryRequest {
    private String content;
    private String nowTry;
}