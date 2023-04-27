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
    private String nickname;

    @Builder
    public CommentPostDto(String content,
                          Long worldCupId,
                          String nickname) {
        this.content = content;
        this.worldCupId = worldCupId;
        this.nickname = nickname;
    }

    public Member getMember() {
        if (this.memberId == null) {
            return null;
        }
        Member member = new Member();
        member.setId(this.memberId);
        return member;
    }

    public String getNickname() {
        if (this.memberId != null) { // 멤버가 존재하면 닉네임 설정 하지 않음
            return null;
        } else if (this.nickname != null) { // 닉네임을 입력했으면 닉네임을 리턴
            return this.nickname;
        }
        return "익명"; // 멤버도 없고, 닉네임도 없으면 기본 닉네임으로 설정
    }

    public WorldCup getWorldCup() {
        WorldCup worldCup = new WorldCup();
        worldCup.setId(this.worldCupId);
        return worldCup;
    }
}
