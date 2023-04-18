package com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.global.common.Auditable;
import jakarta.persistence.*;
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

    // 공개 or 비공개 여부
    @Column(nullable = false)
    private Boolean visibility = true;

    // 해당 월드컵이 실행된 횟수
    @Column(nullable = false)
    private Integer playCount = 0;

    // 해당 월드컵을 만든 멤버
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

//                candidates 보류
}
