package com.example.demo.domain.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private String userId;     // 사용자 ID
    private String orderId;    // 판매자 ID
    private String itemId;     // 물품 ID
    private String itemName;   // 물품명
    private int quantity;      // 물품 수량
    private int totalAmount;   // 결제 금액
    private int taxFreeAmount = 0; // 상품 비과세 금액
}