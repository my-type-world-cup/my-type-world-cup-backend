package com.mytypeworldcup.mytypeworldcup.domain.comment.entity;

import com.mytypeworldcup.mytypeworldcup.domain.like.entity.Like;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import com.mytypeworldcup.mytypeworldcup.global.common.Auditable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private String candidateName;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", nullable = true, updatable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "WORLD_CUP_ID", nullable = false, updatable = false)
    private WorldCup worldCup;

    @OneToMany(mappedBy = "comment", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Like> likes = new ArrayList<>();

    @Builder
    public Comment(String content,
                   String candidateName,
                   Member member,
                   WorldCup worldCup) {
        this.content = content;
        this.candidateName = candidateName;
        this.member = member;
        this.worldCup = worldCup;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        if (this.member == null) {
            return null;
        }
        return this.member.getId();
    }

    public String getNickname() {
        if (this.member == null) {
            return null;
        }
        return this.member.getNickname();
    }

    public Long getWorldCupId() {
        return this.worldCup.getId();
    }

    public Integer getLikesCount() {
        return this.likes.size();
    }
}
