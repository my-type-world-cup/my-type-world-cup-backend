package com.mytypeworldcup.mytypeworldcup.domain.member.exception;

import com.mytypeworldcup.mytypeworldcup.global.error.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberExceptionCode implements ExceptionCode {
    MEMBER_NOT_FOUND(404, "요청하신 회원을 찾을 수 없습니다."),
    MEMBER_EXISTS(409, "이미 존재하는 회원입니다."),
    경고합니다(400,"테스트용 경고입니다");

    private int status;

    private String message;
}

