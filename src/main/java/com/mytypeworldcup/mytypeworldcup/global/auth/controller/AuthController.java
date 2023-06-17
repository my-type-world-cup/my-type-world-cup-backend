package com.mytypeworldcup.mytypeworldcup.global.auth.controller;

import com.mytypeworldcup.mytypeworldcup.global.auth.entity.RefreshToken;
import com.mytypeworldcup.mytypeworldcup.global.auth.service.RefreshService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final RefreshService refreshService;

    @PostMapping("/auth/refresh")
    public ResponseEntity getAccessTokenByRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        refreshService.refreshAccessToken(request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("v1/auth/refresh")
    public ResponseEntity refresh(@CookieValue(value = "RefreshToken") Cookie refreshTokenCookie, HttpServletResponse response) {

        String accessToken = refreshService.refreshAccessToken(refreshTokenCookie, response);

        return ResponseEntity.ok(accessToken);
    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity logout(Authentication authentication) {
        refreshService.logout(authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
