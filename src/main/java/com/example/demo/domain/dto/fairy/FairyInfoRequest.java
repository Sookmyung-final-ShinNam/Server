package com.example.demo.domain.dto.fairy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FairyInfoRequest {
    private String name;
    private Integer age;

    @Schema(description = "성별", example = "MALE", allowableValues = {"MALE", "FEMALE"})
    private String gender;
    private String personality;
    private String appearance;
}