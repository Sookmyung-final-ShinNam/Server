package com.example.demo.entity.base.gpt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserTextAnalysis {

    @Id
    private Long id; // PK로 사용할 필드

    private String userAnswer;

}