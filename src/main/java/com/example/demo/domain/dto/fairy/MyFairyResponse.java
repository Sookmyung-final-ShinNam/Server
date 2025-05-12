package com.example.demo.domain.dto.fairy;

import com.example.demo.domain.dto.FairyTale.FairyTaleInfoResponse;
import com.example.demo.entity.base.Fairy;
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