package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CandidateResponseDto {
    private Long id;
    private String name;
    private String image;
    private Integer finalWinCount;
    private Integer winCount;
    private Integer matchCount;
    private Integer matchUpGameCount;
    private Long worldCupId;

    @Builder
    public CandidateResponseDto(Long id,
                                String name,
                                String image,
                                Integer finalWinCount,
                                Integer winCount,
                                Integer matchCount,
                                Integer matchUpGameCount,
                                Long worldCupId) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.finalWinCount = finalWinCount;
        this.winCount = winCount;
        this.matchCount = matchCount;
        this.matchUpGameCount = matchUpGameCount;
        this.worldCupId = worldCupId;
    }
}
