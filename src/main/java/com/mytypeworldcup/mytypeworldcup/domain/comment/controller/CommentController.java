package com.mytypeworldcup.mytypeworldcup.domain.comment.controller;

import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.service.CommentService;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import com.mytypeworldcup.mytypeworldcup.global.common.PageResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final MemberService memberService;
    private final WorldCupService worldCupService;

    @PostMapping("/comments")
    public ResponseEntity postComment(Authentication authentication,
                                      @Valid @RequestBody CommentPostDto commentPostDto) {
        if (authentication != null) { // 로그인 했을 경우
            Long memberId = memberService.findMemberIdByEmail(authentication.getName()); // 현재 로그인한 멤버 확인
            commentPostDto.setMemberId(memberId);
        }

        worldCupService.findWorldCup(commentPostDto.getWorldCupId()); // 월드컵 존재여부 확인

        CommentResponseDto response = commentService.createComment(commentPostDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/comments")
    public ResponseEntity getCommentsByWorldCupId(Authentication authentication,
                                                  @Positive @RequestParam(required = false, defaultValue = "1") int page,
                                                  @Positive @RequestParam(required = false, defaultValue = "5") int size,
                                                  @RequestParam(required = false, defaultValue = "likesCount") String sort,
                                                  @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                                  @Positive @RequestParam Long worldCupId) {
        Long memberId = null;
        if (authentication != null) {
            memberId = memberService.findMemberIdByEmail(authentication.getName()); // 현재 로그인한 멤버 확인
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, direction, sort);
        Page<CommentResponseDto> responseDtos = commentService.findCommentsByWorldCupId(worldCupId, memberId, pageRequest);

        return ResponseEntity.ok(new PageResponseDto(responseDtos));
    }

}
