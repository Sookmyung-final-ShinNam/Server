package com.example.demo.service;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.payment.ApproveResponse;
import com.example.demo.domain.dto.payment.PaymentRequest;
import com.example.demo.domain.dto.payment.ReadyResponse;

public interface PaymentService {

    // 결제 준비
    ApiResponse<ReadyResponse> preparePayment(PaymentRequest paymentRequest);

    // 결제 승인
    ApiResponse<ApproveResponse> approvePayment(String pgToken, String tid);

}