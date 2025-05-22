package com.example.demo.domain.repository;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.entity.FairyTale;
import com.example.demo.domain.entity.enums.Type;
import com.example.demo.extra.fairyTale.dto.FairyTaleResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FairyTaleRepository extends JpaRepository<FairyTale, Long> {

    // 특정 유저가 만든 모든 동화를 조회
    List<FairyTaleResponse> findAllByUser(User user);

    List<FairyTaleResponse> findAllByUserAndType(User user, Type type);

    List<FairyTaleResponse> findAllByUserAndIsFavorite(User user, Boolean isFavorite);
}