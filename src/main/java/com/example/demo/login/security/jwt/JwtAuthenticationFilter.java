package com.example.demo.login.security.jwt;

import jakarta.servlet.http.Cookie;
import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.domain.entity.Token;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.entity.enums.Status;
import com.example.demo.domain.repository.TokenRepository;
import com.example.demo.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@WebFilter("/*")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        logger.info("Incoming request URI: {}", requestURI);

        // 필터를 거치지 않는 경로
        if (isPermittedRequest(requestURI)) {
            logger.debug("Permitted request, skipping authentication filter.");
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", authorizationHeader);

        // Authorization 헤더가 없거나 Bearer로 시작하지 않을 경우 쿠키에서 복원
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.debug("Authorization header is missing or invalid. Trying to retrieve token from cookie.");

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        authorizationHeader = "Bearer " + cookie.getValue();
                        logger.debug("Recovered Authorization header from cookie: {}", authorizationHeader);
                        break;
                    }
                }
            }
        }

        // 여전히 Authorization이 없으면 예외 발생
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throwException(ErrorStatus.TOKEN_MISSING);
            }

            String token = jwtUtil.getTokenFromHeader(authorizationHeader);
            logger.debug("Extracted token: {}", token);

            // 잘못된 토큰 처리
            if (jwtUtil.isTokenExpired(token)) {
                throwException(ErrorStatus.TOKEN_INVALID_ACCESS_TOKEN);
            }

            String email = jwtUtil.extractUserIdFromToken(token);

            // 유저가 존재하는지 확인
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        try {
                            throwException(ErrorStatus.USER_NOT_FOUND);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    });

            // 사용자 상태 확인 -> 활성화 상태에서만 API 사용 가능 ( 필터를 거치지 않는 경로 제외 )
            if (user.getActive() == Status.INACTIVE) {
                throwException(ErrorStatus.USER_ALREADY_LOGOUT);
            }

            if (user.getActive() == Status.WITHDRAWN) {
                throwException(ErrorStatus.USER_ALREADY_WITHDRAWN);
            }

            Optional<Token> tokenOptional = tokenRepository.findByUser(user);
            if (tokenOptional.isEmpty()) {
                throwException(ErrorStatus.TOKEN_MISSING);
            }

            Token storedToken = tokenOptional.get();
            if (!storedToken.getAccessToken().equals(token)) {
                throwException(ErrorStatus.TOKEN_NOT_FOUND);
            }

            // 사용자 인증 처리
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            logger.error("Authentication error: {}", e.getMessage());
            handleAuthenticationError(response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }


    private boolean isPermittedRequest(String requestURI) {
        return requestURI.startsWith("/swagger-ui/") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/webjars") ||
                requestURI.startsWith("/api/permit/") ||
                requestURI.equals("/favicon.ico") ||
                requestURI.equals("/login") ||
                requestURI.equals("/payment.html") ||
                requestURI.startsWith("/paySuccess.html") ;
    }

    private void throwException(ErrorStatus errorStatus) throws Exception {
        throw new Exception(errorStatus.getMessage());
    }

    private void handleAuthenticationError(HttpServletResponse response, Exception e) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.onFailure(
                ErrorStatus.COMMON_UNAUTHORIZED,
                e.getMessage()
        );

        response.setStatus(ErrorStatus.COMMON_UNAUTHORIZED.getReasonHttpStatus().getHttpStatus().value());
        response.setContentType("application/json; charset=UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

}