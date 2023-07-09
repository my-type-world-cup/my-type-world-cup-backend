package com.mytypeworldcup.mytypeworldcup.global.auth.controller;

import com.mytypeworldcup.mytypeworldcup.global.auth.service.RefreshService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final RefreshService refreshService;

    @PostMapping("/auth/refresh")
    public ResponseEntity<Object> refresh(@CookieValue(value = "RefreshToken") Cookie refreshTokenCookie, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = refreshService.refreshAccessToken(refreshTokenCookie, response);
        String referer = request.getHeader("Referer");
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(UriComponentsBuilder
                        .fromUriString(referer)
                        .path("callback")
                        .queryParam("access_token", accessToken)
                        .build()
                        .toUri())
                .build();
    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity logout(Authentication authentication) {
        refreshService.logout(authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
