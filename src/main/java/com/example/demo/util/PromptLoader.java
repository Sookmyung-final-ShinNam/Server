package com.example.demo.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class PromptLoader {

    public String loadPrompt(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/" + fileName);
            byte[] data = resource.getInputStream().readAllBytes();
            return new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("프롬프트 파일을 읽을 수 없습니다: " + fileName);
        }
    }
}