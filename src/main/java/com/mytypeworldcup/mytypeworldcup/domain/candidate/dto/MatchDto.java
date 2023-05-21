package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MatchDto {
    private Long id;
    private Integer matchUpGameCount;
    private Integer winCount;

    @Builder
    public MatchDto(Long id, Integer matchUpGameCount, Integer winCount) {
        this.id = id;
        this.matchUpGameCount = matchUpGameCount;
        this.winCount = winCount;
    }
}
