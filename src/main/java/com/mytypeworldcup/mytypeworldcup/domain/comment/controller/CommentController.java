package com.mytypeworldcup.mytypeworldcup.domain.comment.controller;

import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.exception.CommentExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.comment.service.CommentService;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final MemberService memberService;
    private final WorldCupService worldCupService;

    @PostMapping("/comments")
    public ResponseEntity postComment(Authentication authentication,
                                      @Valid @RequestBody CommentPostDto commentPostDto) {
        // postDto.nickname == null 일 경우 로그인한 이용자로 간주
        // null이 아니면 로그인하지 않은 이용자로 memberId 세팅을 생략
        if (commentPostDto.getNickname() == null) {
            try {
                Long memberId = memberService.findMemberIdByEmail(authentication.getName()); // 현재 로그인한 멤버 확인
                commentPostDto.setMemberId(memberId);
            } catch (NullPointerException ne) {
                throw new BusinessLogicException(CommentExceptionCode.MISSING_USER_INFO);
            }
        }

        worldCupService.findWorldCup(commentPostDto.getWorldCupId()); // 월드컵 존재여부 확인

        CommentResponseDto response = commentService.createComment(commentPostDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/worldcups/{worldCupId}/comments")
    public ResponseEntity getCommentsByWorldCupId(@PathVariable Long worldCupId) {

        List<CommentResponseDto> responseDtos = commentService.findCommentsByWorldCupId(worldCupId);

        return ResponseEntity.ok(responseDtos);
    }
}
