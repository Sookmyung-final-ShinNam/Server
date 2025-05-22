package com.example.demo.payment.service;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.payment.dto.ApproveResponse;
import com.example.demo.payment.dto.PaymentRequest;
import com.example.demo.payment.dto.ReadyResponse;

public interface PaymentService {

    // 결제 준비
    ApiResponse<ReadyResponse> preparePayment(PaymentRequest paymentRequest);

    // 결제 승인
    ApiResponse<ApproveResponse> approvePayment(String pgToken, String tid);

}