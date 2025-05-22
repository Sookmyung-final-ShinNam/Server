package com.example.demo.domain.entity;

import com.example.demo.domain.entity.enums.Type;
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
@AllArgsConstructor
@Builder
@Table(name = "fairyTale")
public class FairyTale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content; // 줄거리 3줄 요약

    @Enumerated(EnumType.STRING)
    private Type type;

    private Boolean isFavorite;

    @NonNull
    private String background;

    @NonNull
    private String theme1;

    private String theme2;
    private String theme3;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "fairyTale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<FairyParticipation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "fairyTale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PageDraft> pageDrafts = new ArrayList<>();

    @OneToMany(mappedBy = "fairyTale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Page> pages = new ArrayList<>();

    public void updateFavoriteStatus(boolean status) {
        this.isFavorite = status;
    }
}