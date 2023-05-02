package com.mytypeworldcup.mytypeworldcup.domain.like.exception;

import com.mytypeworldcup.mytypeworldcup.global.error.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikeExceptionCode implements ExceptionCode {
    LIKE_EXISTS(409, "해당 댓글에 이미 좋아요를 눌렀습니다."),
    LIKE_NOT_FOUND(404, "좋아요를 찾을 수 없습니다."),
    FORBIDDEN(403, "자신이 누른 좋아요만 취소할 수 있습니다.");
    private int status;
    private String message;
}
