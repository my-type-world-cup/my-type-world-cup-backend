package com.mytypeworldcup.mytypeworldcup.domain.comment.dto;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class CommentPostDto {
    @NotBlank
    private String content;

    @Setter
    private Long memberId;

    private Long worldCupId;

    @Builder
    public CommentPostDto(String content,
                          Long worldCupId) {
        this.content = content;
        this.worldCupId = worldCupId;
    }

    public Member getMember() {
        if (this.memberId == null) {
            return null;
        }
        Member member = new Member();
        member.setId(this.memberId);
        return member;
    }

    public WorldCup getWorldCup() {
        WorldCup worldCup = new WorldCup();
        worldCup.setId(this.worldCupId);
        return worldCup;
    }
}
