package com.example.demo.domain.dto.fairy;

import com.example.demo.domain.dto.fairyTale.FairyTaleInfoResponse;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class MyFairyResponse {

    private Long fairyId;
    private String name;
    private String personality;
    private Integer age;
    private List<String> images;
    private List<String> lines;
    private List<FairyTaleInfoResponse> fairyTales;
}