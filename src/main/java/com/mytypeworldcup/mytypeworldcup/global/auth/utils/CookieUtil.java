package com.mytypeworldcup.mytypeworldcup.global.auth.utils;

import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import com.mytypeworldcup.mytypeworldcup.global.error.CommonExceptionCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

@Slf4j
public class CookieUtil {
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }

        throw new BusinessLogicException(CommonExceptionCode.BAD_REQUEST);
    }

    public static void addHttpOnlyCookie(HttpServletResponse response, String name, String value, int maxAge, String domain) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .sameSite("None") // 동일 사이트, 크로스 사이트에 모두 쿠키 전송 가능
                .secure(false) // https 환경에서만 쿠키 작동
                .path("/")
                .domain("localhost")
                .maxAge(maxAge)
                .httpOnly(true) // 브라우저에서 쿠키에 접근 불가 제한
                .build();
        // 쿠키가 등록이 안된다 ??
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void addHttpOnlyCookie(String name, String value, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None"); // SameSite None 사용하려면 Secure true가 되어야함
        cookie.setDomain("port-0-my-type-world-cup-backend-17xqnr2llgu87xna.sel3.cloudtype.app");
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }


}