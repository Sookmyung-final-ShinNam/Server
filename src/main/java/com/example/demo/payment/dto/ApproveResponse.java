package com.example.demo.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApproveResponse {
    @JsonProperty("approved_at")
    private String approvedAt; // 결제 승인 시각
}