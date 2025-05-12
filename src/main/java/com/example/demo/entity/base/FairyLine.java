package com.example.demo.entity.base;

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

    private String line;

    @ManyToOne
    @JoinColumn(name = "fairy_id")
    private Fairy fairy;
}
