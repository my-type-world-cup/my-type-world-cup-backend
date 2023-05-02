package com.mytypeworldcup.mytypeworldcup.domain.like.dto;

import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class LikePostDto {
    @NotNull
    private Long commentId;
    @Setter
    private Long memberId;

    @Builder
    public LikePostDto(Long commentId) {
        this.commentId = commentId;
    }

    public Comment getComment() {
        if (this.commentId == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(this.commentId);
        return comment;
    }

    public Member getMember() {
        if (this.memberId == null) {
            return null;
        }
        Member member = new Member();
        member.setId(this.memberId);
        return member;
    }
}
