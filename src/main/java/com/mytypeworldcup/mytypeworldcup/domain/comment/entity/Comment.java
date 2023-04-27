package com.mytypeworldcup.mytypeworldcup.domain.comment.entity;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import com.mytypeworldcup.mytypeworldcup.global.common.Auditable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", nullable = true, updatable = false)
    private Member member;

    private String nickname;

    @ManyToOne
    @JoinColumn(name = "WORLD_CUP_ID", nullable = false, updatable = false)
    private WorldCup worldCup;

    @Builder
    public Comment(String content, Member member, String nickname, WorldCup worldCup) {
        this.content = content;
        this.member = member;
        this.nickname = nickname;
        this.worldCup = worldCup;
    }

    public Long getMemberId() {
        if (this.member == null) {
            return null;
        }
        return this.member.getId();
    }

    public String getNickname() {
        if (this.nickname == null) {
            return this.member.getNickname();
        }
        return this.nickname;
    }

    public Long getWorldCupId() {
        return this.worldCup.getId();
    }
}
