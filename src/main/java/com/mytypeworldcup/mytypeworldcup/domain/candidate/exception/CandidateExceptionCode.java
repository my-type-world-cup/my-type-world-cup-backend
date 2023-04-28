package com.mytypeworldcup.mytypeworldcup.domain.candidate.exception;

import com.mytypeworldcup.mytypeworldcup.global.error.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CandidateExceptionCode implements ExceptionCode {
    CANDIDATE_NOT_FOUND(404, "요청하신 CANDIDATE를 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(404, "요청하신 이미지를 찾을 수 없습니다.");
    private int status;
    private String message;
}

