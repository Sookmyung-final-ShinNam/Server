package com.example.demo.entity.base;

import com.example.demo.entity.BaseEntity;
import com.example.demo.entity.enums.Gender;
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
public class Fairy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 요정 ID

    private String name;         // 요정 이름
    private String personality;  // 요정 성격
    private String appearance;   // 요정 외모
    private Integer age;         // 요정 나이

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;       // 요정 성별

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;  // 해당 요정을 소유한 사용자

    @OneToMany(mappedBy = "fairy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FairyParticipation> appearances = new ArrayList<>(); // 요정의 출연 기록들

    @OneToMany(mappedBy = "fairy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FairyLine> lines = new ArrayList<>();

    @OneToMany(mappedBy = "fairy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FairyImage> images = new ArrayList<>();

    @Builder
    public Fairy(Long id, String name, String personality, String appearance, User user, List<FairyParticipation> appearances,
                 Integer age, Gender gender) {
        this.id = id;
        this.name = name;
        this.personality = personality;
        this.appearance = appearance;
        this.user = user;
        this.age = age;
        this.gender = gender;
        this.appearances = (appearances != null) ? appearances : new ArrayList<>();  // appearances 리스트 초기화
    }

}