package com.example.demo.entity.base;

import com.example.demo.entity.BaseEntity;
import com.example.demo.entity.enums.LoginState;
import com.example.demo.entity.enums.Provider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempSocialLogin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String secretKey;

    @Column(nullable = false, length = 50)
    private String userName;

    @Column(nullable = false, length = 50, unique = true)
    private String userId;

    @Column(nullable = false, length = 20)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginState status; // 로그인 or 회원가입

}