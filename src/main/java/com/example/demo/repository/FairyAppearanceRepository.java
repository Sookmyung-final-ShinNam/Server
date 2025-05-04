package com.example.demo.repository;

import com.example.demo.entity.base.FairyAppearance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FairyAppearanceRepository extends JpaRepository<FairyAppearance, Long> {
}