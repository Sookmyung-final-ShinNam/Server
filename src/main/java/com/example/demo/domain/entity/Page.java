package com.example.demo.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이미지
    private String image;

    // 줄거리
    @Column(length = 1000)
    private String plot;

    // 감정 인식 텍스트
    @Column(length = 1000)
    private String emotionText;

    @ManyToOne
    @JoinColumn(name = "faryTale_id")
    @JsonBackReference
    private FairyTale fairyTale;  // 등장한 동화

}