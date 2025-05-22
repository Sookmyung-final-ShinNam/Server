package com.example.demo.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReadyResponse {
    private String tid; // 결제 고유 번호

    @JsonProperty("next_redirect_pc_url")
    private String nextRedirectPcUrl; // PC용 결제 페이지 URL

    @JsonProperty("next_redirect_mobile_url")
    private String nextRedirectMobileUrl; // 모바일용 결제 페이지 URL

    @JsonProperty("created_at")
    private String createdAt; // 결제 생성 시각
}