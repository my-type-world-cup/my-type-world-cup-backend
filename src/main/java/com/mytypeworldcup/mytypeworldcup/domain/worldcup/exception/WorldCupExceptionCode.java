package com.mytypeworldcup.mytypeworldcup.domain.worldcup.exception;

import com.mytypeworldcup.mytypeworldcup.global.error.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorldCupExceptionCode implements ExceptionCode {
    WORLD_CUP_NOT_FOUND(404, "요청하신 월드컵을 찾을 수 없습니다.");
    private int status;
    private String message;
}

