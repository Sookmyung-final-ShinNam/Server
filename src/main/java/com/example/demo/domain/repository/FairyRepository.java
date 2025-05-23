package com.example.demo.domain.repository;

import com.example.demo.extra.fairy.dto.FairyInfoResponse2;
import com.example.demo.domain.entity.Fairy;
import com.example.demo.domain.entity.enums.Gender;
import com.example.demo.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FairyRepository extends JpaRepository<Fairy, Long> {

    // 유저가 만든 모든 요정을 조회
    List<FairyInfoResponse2> findAllByUserEmail(String email);

    // 유저가 만든 특정 성별의 모든 요정을 조회
    List<FairyInfoResponse2> findAllByUserEmailAndGender(String email, Gender gender);

    // 즐겨찾기한 요정 조회
    List<Fairy> findByUserAndIsFavorite(User user, Boolean isFavorite);

    Optional<Fairy> findByName(String name);
}