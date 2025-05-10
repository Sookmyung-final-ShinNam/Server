package com.example.demo.repository;

import com.example.demo.entity.base.Fairy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FairyRepository extends JpaRepository<Fairy, Long> {

    // 특정 유저가 만든 모든 요정을 조회
    List<Fairy> findAllByUserEmail(String username);

}