package com.example.demo.security.oauth.authentication;

import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.code.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String code = request.getParameter("code");
        String state = request.getParameter("state");

        if (code == null || state == null) {
            throw new CustomException(ErrorStatus.OAUTH_PROCESSING_FAILED);
        }

    }
}