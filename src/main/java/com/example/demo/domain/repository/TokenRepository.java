package com.example.demo.domain.repository;

import com.example.demo.domain.entity.Token;
import com.example.demo.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    // 1. 사용자(User)에 대한 토큰을 찾아 반환
    Optional<Token> findByUser(User user);

    // 2. 액세스 토큰을 찾음
    Optional<Token> findByAccessToken(String accessToken);

    // 3. 리프레시 토큰을 찾음
    Optional<Token> findByRefreshToken(String refreshToken);
}