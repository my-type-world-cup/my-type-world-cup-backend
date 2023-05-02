package com.mytypeworldcup.mytypeworldcup.domain.like.controller;

import com.mytypeworldcup.mytypeworldcup.domain.like.dto.LikePostDto;
import com.mytypeworldcup.mytypeworldcup.domain.like.service.LikeService;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final MemberService memberService;
    private final LikeService likeService;

    @PostMapping("/likes")
    public ResponseEntity postLike(Authentication authentication,
                                   @Valid @RequestBody LikePostDto likePostDto) {
        // 멤버아이디 세팅
        Long memberId = memberService.findMemberIdByEmail(authentication.getName());
        likePostDto.setMemberId(memberId);

        Long likeId = likeService.createLike(likePostDto);
        Map<String, Long> response = new HashMap<>();
        response.put("likeId", likeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}