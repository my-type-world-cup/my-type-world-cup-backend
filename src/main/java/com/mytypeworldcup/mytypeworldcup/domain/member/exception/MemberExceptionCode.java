package com.mytypeworldcup.mytypeworldcup.domain.member.exception;

import com.mytypeworldcup.mytypeworldcup.global.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberExceptionCode implements ExceptionCode {
    MEMBER_NOT_FOUND(404, "요청하신 회원을 찾을 수 없습니다.");
    private int status;

    private String message;
}

