package com.mytypeworldcup.mytypeworldcup.global.auth.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;

@Slf4j
public class CookieUtil {
    public static void addHttpOnlyCookie(String name, String value, String domain, int maxAge, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .secure(true)
                .sameSite("None") // SameSite None 사용하려면 Secure true가 되어야함
                .domain(domain)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void addCookie(String name, String value, String domain, int maxAge, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .secure(true)
                .sameSite("None") // SameSite None 사용하려면 Secure true가 되어야함
                .domain(domain) // clientUrl
                .path("/")
                .maxAge(maxAge)
                .httpOnly(false)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void deleteCookie(Cookie cookie, HttpServletResponse response) {
        cookie.setValue("");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}