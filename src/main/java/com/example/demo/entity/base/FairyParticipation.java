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
public class FairyParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 출연 기록 ID

    // 출연하는 요정
    @ManyToOne
    @JoinColumn(name = "fairy_id")
    @JsonBackReference
    private Fairy fairy;  // 출연한 요정

    // 등장하는 동화
    @ManyToOne
    @JoinColumn(name = "faryTale_id")
    @JsonBackReference
    private FairyTale fairyTale;  // 등장한 동화

}