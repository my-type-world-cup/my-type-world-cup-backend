package com.mytypeworldcup.mytypeworldcup.global.auth.controller;

import com.mytypeworldcup.mytypeworldcup.global.auth.service.RefreshService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final RefreshService refreshService;
    @Value("${dolphin.client.url}")
    private String clientUrl;

    @GetMapping("/auth/refresh")
    public ResponseEntity refresh(@CookieValue(value = "RefreshToken") Cookie refreshTokenCookie, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = refreshService.refreshAccessToken(refreshTokenCookie, response);

        URI uri = UriComponentsBuilder
                .fromUriString(clientUrl)
                .path("callback")
                .queryParam("access_token", accessToken)
                .build()
                .toUri();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(uri)
                .build();
    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity logout(Authentication authentication) {
        refreshService.logout(authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
