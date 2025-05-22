package com.example.demo.repository;

import com.example.demo.domain.dto.fairyTale.FairyTaleResponse;
import com.example.demo.entity.base.FairyTale;
import com.example.demo.entity.base.User;
import com.example.demo.entity.enums.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FairyTaleRepository extends JpaRepository<FairyTale, Long> {

    // 특정 유저가 만든 모든 동화를 조회
    List<FairyTaleResponse> findAllByUser(User user);
    List<FairyTaleResponse> findAllByUserAndType(User user, Type type);
    List<FairyTaleResponse> findAllByUserAndIsFavorite(User user, Boolean isFavorite);

    Optional<FairyTale> findByIdAndUserEmail(Long id, String email);
}