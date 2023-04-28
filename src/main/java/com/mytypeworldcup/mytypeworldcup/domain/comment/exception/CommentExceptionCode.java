package com.mytypeworldcup.mytypeworldcup.domain.comment.exception;

import com.mytypeworldcup.mytypeworldcup.global.error.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentExceptionCode implements ExceptionCode {
    MISSING_USER_INFO(400, "유저정보가 누락되었습니다. 로그인을 하거나 닉네임을 입력해주세요.");
    private int status;
    private String message;
}
