package com.example.demo.repository.gpt;

import com.example.demo.entity.base.gpt.StoryFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryFeedbackRepository extends JpaRepository<StoryFeedback, Long> {
}