package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CandidateResponseDto {
    private Long id;
    private String name;
    private String image;
    private String thumb;
    private Integer finalWinCount;
    private Integer winCount;
    private Integer matchUpWorldCupCount;
    private Integer matchUpGameCount;
    private Long worldCupId;

    @Builder
    @QueryProjection
    public CandidateResponseDto(Long id,
                                String name,
                                String image,
                                String thumb,
                                Integer finalWinCount,
                                Integer winCount,
                                Integer matchUpWorldCupCount,
                                Integer matchUpGameCount,
                                Long worldCupId) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.thumb = thumb;
        this.finalWinCount = finalWinCount;
        this.winCount = winCount;
        this.matchUpWorldCupCount = matchUpWorldCupCount;
        this.matchUpGameCount = matchUpGameCount;
        this.worldCupId = worldCupId;
    }
}
