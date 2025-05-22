package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fairyLine")
public class FairyLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 명대사
    private String line;

    @ManyToOne
    @JoinColumn(name = "fairy_id")
    private Fairy fairy;

}