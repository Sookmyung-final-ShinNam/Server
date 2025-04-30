package com.example.demo.repository.gpt;

import com.example.demo.entity.base.gpt.UserAnswerCorrection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerCorrectionRepository extends JpaRepository<UserAnswerCorrection, Long> {
}