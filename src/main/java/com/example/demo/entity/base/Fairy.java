package com.example.demo.entity.base;

import com.example.demo.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
public class Fairy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 이름

    private String personality; // 성격

    private String appearance; // 생김새

    // 요정의 주인 (소유자)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "fairies")
    private List<FairyTale> fairyTales = new ArrayList<>(); // 기본값을 설정

    // 모든 필드를 초기화하는 생성자를 명시적으로 추가
    public Fairy(Long id, String name, String personality, String appearance, User user, List<FairyTale> fairyTales) {
        this.id = id;
        this.name = name;
        this.personality = personality;
        this.appearance = appearance;
        this.user = user;
        this.fairyTales = fairyTales;
    }

}