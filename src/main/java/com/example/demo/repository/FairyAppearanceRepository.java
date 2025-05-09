package com.example.demo.repository;

import com.example.demo.entity.base.FairyParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FairyAppearanceRepository extends JpaRepository<FairyParticipation, Long> {
}