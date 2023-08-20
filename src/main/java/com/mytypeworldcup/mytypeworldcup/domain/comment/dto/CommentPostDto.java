package com.mytypeworldcup.mytypeworldcup.domain.comment.dto;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Getter
public class CommentPostDto {
    @NotNull
    @Length(min = 1, max = 255)
    private String content;
    @Length(max = 50)
    private String candidateName;
    private Long worldCupId;
    @Setter
    private Long memberId;

    @Builder
    public CommentPostDto(String content,
                          String candidateName,
                          Long worldCupId) {
        this.content = content;
        this.candidateName = candidateName;
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
