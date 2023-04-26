package com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.global.common.Auditable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Column(nullable = false)
    private String title;

    // 월드컵의 설명
    private String description;

    /**
     * visibility 와 password 둘다필요한가 ??
     */
    // 공개 or 비공개 여부 true=공개
    @Column(nullable = false)
    private Boolean visibility;

    // 비밀번호 -> 공개일경우 해당 필드 null, 비공개일경우 Null이 아님
    private String password;

    // 해당 월드컵이 실행된 횟수
    @Column(nullable = false)
    private Integer playCount = 0;

    // 해당 월드컵을 만든 멤버
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

//                candidates 보류

    @Builder
    public WorldCup(String title,
                    String description,
                    Boolean visibility,
                    String password,
                    Member member) {
        this.title = title;
        this.description = description;
        this.visibility = visibility;
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
}
