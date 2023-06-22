package com.mytypeworldcup.mytypeworldcup.global.auth.controller;

import com.mytypeworldcup.mytypeworldcup.global.auth.service.RefreshService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final RefreshService refreshService;

    @PostMapping("/auth/refresh")
    public ResponseEntity refresh(@CookieValue(value = "RefreshToken") Cookie refreshTokenCookie, HttpServletResponse response) {
        String accessToken = refreshService.refreshAccessToken(refreshTokenCookie, response);
        response.addHeader("Authorization", accessToken);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity logout(Authentication authentication) {
        refreshService.logout(authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
