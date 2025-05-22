package com.example.demo.domain.entity;

import com.example.demo.domain.entity.enums.Provider;
import com.example.demo.domain.entity.enums.Status;
import com.example.demo.login.service.UserRelations;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends UserRelations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이메일 - 30자 이내, 고유하게 설정
    @Column(nullable = false, length = 30, unique = true)
    private String email;

    // 닉네임 - 20자 이내
    @Column(nullable = false, length = 20)
    private String nickname;

    // provider > 현재는 kakao 만 있음
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    // 포인트
    @Column(nullable = false)
    @Builder.Default
    private Integer point = 0;

    // 보유 가능한 최대 요정 수
    @Column(nullable = false)
    @Builder.Default
    private Integer maxFairyNum = 5;

    // 보유 가능한 최대 동화 수
    @Column(nullable = false)
    @Builder.Default
    private Integer maxFairyTaleNum = 5;

    // active 는 3개 ( 활성 = 로그인, 비활성 = 로그아웃, 탈퇴 = 일정시간 이상 지속시 자동으로 회원 정보 삭제 ) 중 하나
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status active;

    // 탈퇴 시간 기록 ( 이 시간으로부터 일정 시간 이상 지나면 자동으로 사용자 정보가 삭제됨. 재로그인시 탈퇴 시간이 삭제되고 활성화 상태로 설정됨. )
    @Column
    private LocalDateTime withdrawAt;

    // 소유한 요정들
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Fairy> fairies = new ArrayList<>();

    // 소유한 동화들
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<FairyTale> fairyTales = new ArrayList<>();


    // 메서드

    // 1. 사용자 상태를 탈퇴로 설정 & 탈퇴 시간 기록
    public void withdraw() {
        this.active = Status.WITHDRAWN;
        this.withdrawAt = LocalDateTime.now();
    }

    // 2. 사용자 상태를 활성화로 설정 & 탈퇴 시간을 삭제
    public void activate() {
        this.active = Status.ACTIVE;
        this.withdrawAt = null;
    }

    // 3. 사용자 상태를 비활성화로 설정
    public void deactivate() {
        this.active = Status.INACTIVE;
    }

    // 4. 유저가 보유 가능한 요정 수를 넘지 않는지 체크
    public boolean isFairyLimited() {
        return fairies.size() >= maxFairyNum;
    }

    // 5. 유저가 보유 가능한 동화 수를 넘지 않는지 체크
    public boolean isFairyTaleLimited() {
        return fairyTales.size() >= maxFairyTaleNum;
    }
}