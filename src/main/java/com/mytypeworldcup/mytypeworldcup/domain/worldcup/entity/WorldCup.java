package com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
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
public class WorldCup extends Auditable {

    /**
     * 월드컵을 의미하는 테이블로써, 본격적인 게임의 제목을 뜻한다
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 월드컵의 제목
    @Column(nullable = false, length = 50)
    private String title;

    // 월드컵의 설명
    @Column(length = 200)
    private String description;

    // 비밀번호 -> 공개일경우 해당 필드 null, 비공개일경우 Null이 아님
    @Column(length = 4)
    private String password;

    // 해당 월드컵이 실행된 횟수
    @Column(nullable = false)
    private Integer playCount = 0;

    // 해당 월드컵을 만든 멤버
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

    @OneToMany(mappedBy = "worldCup", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Candidate> candidates = new ArrayList<>();

    @OneToMany(mappedBy = "worldCup", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public WorldCup(String title,
                    String description,
                    String password,
                    Member member) {
        this.title = title;
        this.description = description;
        this.password = password;
        this.member = member;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        if (member == null) {
            return null;
        }
        return member.getId();
    }

    public void updatePlayCount() {
        this.playCount++;
    }

    public int getCandidatesCount() {
        return this.candidates.size();
    }

    public boolean getVisibility() {
        if (this.password == null) {
            return true;
        }
        return false;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
