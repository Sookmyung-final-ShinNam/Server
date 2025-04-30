package com.example.demo.entity.base.payment;

import com.example.demo.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String userId; // 구매자 ID

    @NotNull
    @Column(nullable = false)
    private String orderId; // 판매자 ID

    @NotNull
    @Column(nullable = false)
    private String itemId; // 물건 ID

    @NotNull
    @Column(nullable = false)
    private String itemName; // 물건 이름

    @NotNull
    @Column(nullable = false)
    private int quantity; // 수량

    @NotNull
    @Column(nullable = false)
    private int amount; // 결제 금액

    @NotNull
    @Column(nullable = false, unique = true)  // unique 제약 조건 추가
    private String tid; // 결제 고유 번호

    @NotNull
    @Column(nullable = false)
    private String status; // 결제 상태 (READY, APPROVED, CANCELLED)

    private LocalDateTime approvedAt; // 결제 승인 일시 (APPROVED 상태일 때)
}