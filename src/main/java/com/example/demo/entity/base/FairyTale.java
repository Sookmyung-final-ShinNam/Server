package com.example.demo.entity.base;

import com.example.demo.entity.BaseEntity;
import com.example.demo.entity.enums.Type;
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
@Table(name = "faryTale")
public class FairyTale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    private String question;

    @Enumerated(EnumType.STRING)
    private Type type;

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
    private List<FairyParticipation> appearances = new ArrayList<>();

    @OneToMany(mappedBy = "fairyTale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Page> pages = new ArrayList<>();
}