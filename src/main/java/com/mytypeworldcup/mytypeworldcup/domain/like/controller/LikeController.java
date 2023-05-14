package com.mytypeworldcup.mytypeworldcup.domain.like.controller;

import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.comment.service.CommentService;
import com.mytypeworldcup.mytypeworldcup.domain.like.service.LikeService;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final MemberService memberService;
    private final CommentService commentService;
    private final LikeService likeService;

    @PostMapping("/comments/{commentId}/likes")
    public ResponseEntity postLike(Authentication authentication,
                                   @Positive @PathVariable Long commentId) {

        Member member = memberService.findVerifiedMemberByEmail(authentication.getName());
        Comment comment = commentService.findVerifiedCommentById(commentId);

        likeService.createLike(member, comment);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/comments/{commentId}/likes")
    public ResponseEntity deleteLike(Authentication authentication,
                                     @Positive @PathVariable Long commentId) {

        Member member = memberService.findVerifiedMemberByEmail(authentication.getName());
        Comment comment = commentService.findVerifiedCommentById(commentId);

        likeService.deleteLike(member, comment);

        return ResponseEntity.noContent().build();
    }
}
