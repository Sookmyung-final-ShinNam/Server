package com.example.demo.entity.base;

import com.example.demo.entity.enums.Provider;
import com.example.demo.entity.enums.Status;
import com.example.demo.entity.enums.Role;
import com.example.demo.entity.relations.UserRelations;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(nullable = false, length = 50, unique = true)
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
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status active;

    @Column
    private LocalDateTime withdrawTime;

    public void withdraw() {
        this.active = Status.WITHDRAWN;
        this.withdrawTime = LocalDateTime.now();
    }

    public void activate() {
        this.active = Status.ACTIVE;
        this.withdrawTime = null;
    }

    public void deactivate() {
        this.active = Status.INACTIVE;
    }

}