package com.mytypeworldcup.mytypeworldcup.global.auth.controller;

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
    public ResponseEntity refresh(@CookieValue(value = "RefreshToken") Cookie refreshTokenCookie, HttpServletRequest request, HttpServletResponse response) {
        refreshService.refreshAccessToken(refreshTokenCookie, request.getServerName(), response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/test")
    public ResponseEntity test(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("getServerName", request.getServerName());
        map.put("getRequestURI", request.getRequestURI());
        map.put("toString", request.toString());

        return ResponseEntity.ok(map);
    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity logout(Authentication authentication) {
        refreshService.logout(authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
