package com.example.demo.security.oauth.authentication;

import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.code.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuthLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        log.error("LOGIN FAILED: {}", exception.getMessage());
        throw new CustomException(ErrorStatus.OAUTH_LOGIN_FAILED);
    }
}