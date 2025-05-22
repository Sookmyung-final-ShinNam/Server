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
public class PageDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 해당 장의 질문
    private String question;

    // 해당 장의 답변
    private String answer;

    // 해당 장의 다음이야기
    @Column(length = 1000)
    private String next;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fairyTale_id")
    @JsonBackReference
    private FairyTale fairyTale;

}