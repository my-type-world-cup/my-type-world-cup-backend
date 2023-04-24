package com.mytypeworldcup.mytypeworldcup.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonExceptionCode implements ExceptionCode {
    SERVICE_UNAVAILABLE(503, "서버에서 일시적으로 요청을 처리할 수 없습니다. 잠시 후 다시 시도해주세요.");
    private int status;
    private String message;
}