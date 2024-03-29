package com.mytypeworldcup.mytypeworldcup.domain.candidate.entity;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import com.mytypeworldcup.mytypeworldcup.global.common.Auditable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Candidate extends Auditable {

    /**
     * 월드컵에 참가하는 후보(선수, 참가자)들을 뜻하는 테이블
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 후보의 이름 또는 간략한 설명으로써, 이용자들에게 보여질 이름
    @Column(nullable = false, length = 50)
    private String name;

    // 이미지 링크
    @Column(nullable = false)
    private String image;
    private String thumb;

    // 최종 우승 횟수 - 1등을 한 횟수를 의미함
    @Column(nullable = false)
    private Integer finalWinCount = 0;

    // 경기 승리 횟수 - 해당 선수가 출전한 전체 1:1 대결 중 승리한 횟수를 의미함
    @Column(nullable = false)
    private Integer winCount = 0;

    // 월드컵에 출전한 횟수
    @Column(nullable = false)
    private Integer matchUpWorldCupCount = 0;

    // 경기 출전 횟수 - 해당 후보가 출전된 전체 1:1 대결 수
    @Column(nullable = false)
    private Integer matchUpGameCount = 0;

    // 해당 후보가 속한 월드컵
    @ManyToOne
    @JoinColumn(name = "WORLD_CUP_ID", nullable = false, updatable = false)
    private WorldCup worldCup;

    @Builder
    public Candidate(String name,
                     String image,
                     String thumb,
                     WorldCup worldCup) {
        this.name = name;
        this.image = image;
        this.thumb = thumb;
        this.worldCup = worldCup;
    }

    public Long getWorldCupId() {
        return this.worldCup.getId();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void updateThumb(String thumb) {
        this.thumb = thumb;
    }

    public void updateFinalWinCount() {
        this.finalWinCount++;
    }

    public void updateWinCount(int winCount) {
        this.winCount += winCount;
    }

    public void updateMatchUpWorldCupCount() {
        this.matchUpWorldCupCount++;
    }

    public void updateMatchUpGameCount(int matchUpGameCount) {
        this.matchUpGameCount += matchUpGameCount;
    }
}
