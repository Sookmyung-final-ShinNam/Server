package com.example.demo.domain.repository;

import com.example.demo.domain.entity.FairyLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FairyLineRepository extends JpaRepository<FairyLine, Long> {

    // 요정 번호로 명대사 조회
    Optional<FairyLine> findFirstByFairyId(Long fairyId);
}