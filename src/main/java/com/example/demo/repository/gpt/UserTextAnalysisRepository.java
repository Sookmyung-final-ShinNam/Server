package com.example.demo.repository.gpt;

import com.example.demo.entity.base.gpt.UserTextAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTextAnalysisRepository extends JpaRepository<UserTextAnalysis, Long> {
}