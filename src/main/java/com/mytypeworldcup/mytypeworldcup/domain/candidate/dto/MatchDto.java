package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MatchDto {
    @Min(1)
    private Long id;
    @Min(1)
    private Integer matchUpGameCount;
    @Min(0)
    private Integer winCount;

    @Builder
    public MatchDto(Long id, Integer matchUpGameCount, Integer winCount) {
        this.id = id;
        this.matchUpGameCount = matchUpGameCount;
        this.winCount = winCount;
    }
}
