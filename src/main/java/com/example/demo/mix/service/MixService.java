package com.example.demo.mix.service;

import com.example.demo.base.api.ApiResponse;
import com.example.demo.mix.dto.MixFairyTaleRequest;

public interface MixService {

    ApiResponse mixFairyTale(String userId, MixFairyTaleRequest request);


}