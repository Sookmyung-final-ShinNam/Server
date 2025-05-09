package com.example.demo.security.jwt;

import com.example.demo.entity.enums.Status;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final String secretKey;
    private final Long accessExpirationMillis;
    private final Long refreshExpirationMillis;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final String timeZone;

    public JwtUtil(@Value("${spring.jwt.secret}") String secretKey,
                   @Value("${spring.jwt.expiration.access-token}") Long accessExpirationMillis,
                   @Value("${spring.jwt.expiration.refresh-token}") Long refreshExpirationMillis,
                   @Value("${spring.time-zone}") String timeZone,
                   UserRepository userRepository,
                   TokenRepository tokenRepository) {
        this.secretKey = secretKey;
        this.accessExpirationMillis = accessExpirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
        this.timeZone = timeZone;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }


    private Key getSigningKey() {
        log.debug("getSigningKey() 호출됨. secretKey: {}", secretKey);
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String userId) {
        log.info("액세스 토큰 발행 시작. 사용자 ID: {}", userId);

        // 현재 시간 기준으로 액세스 토큰 만료 시간 계산 (지정된 시간대 사용)
        LocalDateTime now = LocalDateTime.now(ZoneId.of(timeZone));
        Date currentTime = Date.from(now.atZone(ZoneId.of(timeZone)).toInstant());
        Date accessTokenExpiration = Date.from(now.plusSeconds(accessExpirationMillis).atZone(ZoneId.of(timeZone)).toInstant());

        // 액세스 토큰 생성
        String token = Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(currentTime)
                .setExpiration(accessTokenExpiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        log.debug("생성된 액세스 토큰: {}", token);
        return token;
    }

    public String generateRefreshToken(String userId) {
        log.info("리프레쉬 토큰 발행 시작. 사용자 ID: {}", userId);

        // 현재 시간 기준으로 리프레쉬 토큰 만료 시간 계산 (지정된 시간대 사용)
        LocalDateTime now = LocalDateTime.now(ZoneId.of(timeZone));
        Date currentTime = Date.from(now.atZone(ZoneId.of(timeZone)).toInstant());
        Date refreshTokenExpiration = Date.from(now.plusSeconds(refreshExpirationMillis).atZone(ZoneId.of(timeZone)).toInstant());

        // 리프레쉬 토큰 생성
        String token = Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(currentTime)
                .setExpiration(refreshTokenExpiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        log.debug("생성된 리프레쉬 토큰: {}", token);
        return token;
    }

    public String getTokenFromHeader(String authorizationHeader) {
        log.debug("Authorization 헤더 확인: {}", authorizationHeader);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("유효하지 않은 Authorization 헤더: {}", authorizationHeader);
            throw new RuntimeException("유효하지 않은 Authorization 헤더");
        }
        return authorizationHeader.substring(7);
    }

    public String extractUserIdFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", String.class);
        } catch (JwtException e) {
            log.warn("유효하지 않은 토큰입니다.");
            throw new RuntimeException("유효하지 않은 토큰");
        }
    }

    private boolean isTokenExpiredOrValid(String token, boolean isAccessToken) {
        try {
            String userId = extractUserIdFromToken(token);

            // 토큰이 데이터베이스에 존재하는지 확인
            if (!isTokenInDatabase(token)) {
                log.warn("해당 토큰이 데이터베이스에 존재하지 않습니다. token: {}", token);
                return false;  // 토큰이 데이터베이스에 없다면 만료로 간주
            }

            // 액세스 토큰 사용 가능 여부 확인 -> 엑세스 토큰은 활성화 상태에서만 사용 가능
            if (isAccessToken && !isActiveUser(userId)) {
                log.warn("사용자가 비활성 상태입니다. userId: {}", userId);
                return false;
            }

            // 리프레쉬 토큰 사용 가능 여부 확인 -> 리프레쉬 토큰은 활성화 혹은 비활성화 상태에서만 사용 가능
            if (!isAccessToken && isDeletedUser(userId)) {
                log.warn("사용자가 탈퇴 상태입니다. userId: {}", userId);
                return false;
            }

            // 액세스 토큰 또는 리프레쉬 토큰의 만료 여부 확인
            Date expirationDate = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            // 액세스 토큰은 만료되지 않았으면 false, 리프레쉬 토큰은 만료되지 않았으면 true 반환
            return isAccessToken ? expirationDate.before(new Date()) : expirationDate.after(new Date());

        } catch (JwtException e) {
            log.warn("유효하지 않은 토큰입니다. {}", e.getMessage());
            return true;
        }
    }


    // 1. 엑세스 토큰 유효성 검사
    public boolean isTokenExpired(String accessToken) {
        return isTokenExpiredOrValid(accessToken, true);
    }

    // 2. 리프레쉬 토큰 유효성 검사
    public boolean isValidRefreshToken(String refreshToken) {
        return isTokenExpiredOrValid(refreshToken, false);
    }

    // 3. 사용자 활성 상태 확인 메서드
    private boolean isActiveUser(String username) {
        return userRepository.findByUsername(username)
                .map(user -> Status.ACTIVE.equals(user.getActive())) // "ACTIVE" 상태 확인
                .orElse(false); // username이 없으면 false 반환
    }

    // 4. 사용자 탈퇴 상태 확인 메서드
    private boolean isDeletedUser(String username) {
        return userRepository.findByUsername(username)
                .map(user -> Status.WITHDRAWN.equals(user.getActive())) // "WITHDRAWN" 상태 확인
                .orElse(true); // username이 없으면 true 반환, 즉 탈퇴 상태로 간주
    }

    // 5. 토큰이 데이터베이스에 존재하는지 확인하는 메서드
    private boolean isTokenInDatabase(String token) {
        return tokenRepository.findByAccessToken(token).isPresent() || tokenRepository.findByRefreshToken(token).isPresent();
    }

}