package com.example.demo.repository;

import com.example.demo.entity.base.FairyTale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FairyTaleRepository extends JpaRepository<FairyTale, Long> {

    // 특정 유저가 만든 모든 동화를 조회
    List<FairyTale> findAllByUser_UserId(String userId);

}