package com.mytypeworldcup.mytypeworldcup.domain.like.exception;

import com.mytypeworldcup.mytypeworldcup.global.error.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikeExceptionCode implements ExceptionCode {
    LIKE_EXISTS(409, "해당 댓글에 이미 좋아요를 눌렀습니다.");
    private int status;
    private String message;
}
