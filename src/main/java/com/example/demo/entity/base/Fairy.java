package com.example.demo.entity.base;

import com.example.demo.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Fairy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 요정 ID

    private String name;         // 요정 이름
    private String personality;  // 요정 성격
    private String appearance;   // 요정 외모

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;  // 해당 요정을 소유한 사용자

    @OneToMany(mappedBy = "fairy", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<FairyAppearance> appearances = new ArrayList<>(); // 요정의 출연 기록들

    public Fairy(Long id, String name, String personality, String appearance, User user, List<FairyAppearance> appearances) {
        this.id = id;
        this.name = name;
        this.personality = personality;
        this.appearance = appearance;
        this.user = user;
        this.appearances = (appearances != null) ? appearances : new ArrayList<>();  // appearances 리스트 초기화
    }

}