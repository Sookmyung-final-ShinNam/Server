package com.example.demo.repository;

import com.example.demo.entity.base.Fairy;
import com.example.demo.entity.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FairyRepository extends JpaRepository<Fairy, Long> {

    // 유저가 만든 모든 요정을 조회
    List<Fairy> findAllByUserEmail(String email);

    // 유저가 만든 특정 성별의 모든 요정을 조회
    List<Fairy> findAllByUserEmailAndGender(String email, Gender gender);
}