package com.mytypeworldcup.mytypeworldcup.global.auth.utils;


import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import com.mytypeworldcup.mytypeworldcup.global.error.CommonExceptionCode;
import jakarta.servlet.http.HttpServletRequest;

// Header 정보를 가져오는 유틸 클래스
// Auth header 가져옴
public class HeaderUtil {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    private final static String HEADER_REFRESH_TOKEN = "RefreshToken";

    public static String getAccessToken(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_AUTHORIZATION);
        // 헤더에 인증정보가 없거나, Bearer 로 시작하지 않는 경우 예외 발생
        if (headerValue == null || !headerValue.startsWith(TOKEN_PREFIX)) {
            throw new BusinessLogicException(CommonExceptionCode.BAD_REQUEST);
        }

        return headerValue.substring(TOKEN_PREFIX.length());
    }

    public static String getHeaderRefreshToken(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_REFRESH_TOKEN);

        if (headerValue == null) {
            return null;
        }

        if (headerValue.startsWith(TOKEN_PREFIX)) {
            return headerValue.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}