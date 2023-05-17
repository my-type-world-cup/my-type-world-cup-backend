package com.mytypeworldcup.mytypeworldcup.global.auth.exception;

import com.mytypeworldcup.mytypeworldcup.global.error.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthExceptionCode implements ExceptionCode {
    REFRESH_TOKEN_NOT_FOUND(404, "유효한 리프레쉬 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_EXPIRED(401, "만료된 리프레쉬 토큰입니다. 다시 로그인해주세요."),
    UNAUTHORIZED(401, "유효하지 않은 리프레쉬 토큰입니다."),
    ;
    private int status;
    private String message;
}
