package com.example.demo.entity.base;

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

    // 한 장의 이미지 링크
    private String image;

    // 해당 장의 질문
    private String question;

    // 해당 장의 답변
    private String answer;

    // 한 장의 줄거리 (질문과 답변도 포함을 할까..?)
    private String plot;

    // 등장하는 동화
    @ManyToOne
    @JoinColumn(name = "faryTale_id")
    private FairyTale fairyTale;  // 등장한 동화
}
