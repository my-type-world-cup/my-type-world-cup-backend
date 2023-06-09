package com.mytypeworldcup.mytypeworldcup.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonExceptionCode implements ExceptionCode {
    INVALID_PASSWORD(401, "비밀번호가 유효하지 않습니다."),
    SERVICE_UNAVAILABLE(503, "서버에서 일시적으로 요청을 처리할 수 없습니다. 잠시 후 다시 시도해주세요."),
    BAD_REQUEST(400, "잘못된 요청입니다."),
    FORBIDDEN(403, "해당 리소스에 접근 권한이 없습니다.");
    private int status;
    private String message;
}
