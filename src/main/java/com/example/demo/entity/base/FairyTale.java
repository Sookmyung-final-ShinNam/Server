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
public class FairyTale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 제목

    @Lob
    private String content; // 내용

    // 동화의 주인 (소유자)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "fairy_tale_fairy",
            joinColumns = @JoinColumn(name = "fairy_tale_id"),
            inverseJoinColumns = @JoinColumn(name = "fairy_id")
    )
    private List<Fairy> fairies = new ArrayList<>();

    // 모든 필드를 초기화하는 생성자 추가
    public FairyTale(Long id, String title, String content, User user, List<Fairy> fairies) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.fairies = fairies;
    }

}