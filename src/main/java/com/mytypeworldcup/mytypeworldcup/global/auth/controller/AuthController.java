package com.mytypeworldcup.mytypeworldcup.global.auth.controller;

import com.mytypeworldcup.mytypeworldcup.global.auth.service.RefreshService;
import com.mytypeworldcup.mytypeworldcup.global.common.SingleResponseDto;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final RefreshService refreshService;

    @GetMapping("/auth/refresh")
    public ResponseEntity refresh(@CookieValue(value = "RefreshToken") Cookie refreshTokenCookie) {
        String accessToken = refreshService.refreshAccessToken(refreshTokenCookie);
        return ResponseEntity.ok(new SingleResponseDto(accessToken));
    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity logout(Authentication authentication) {
        refreshService.logout(authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
