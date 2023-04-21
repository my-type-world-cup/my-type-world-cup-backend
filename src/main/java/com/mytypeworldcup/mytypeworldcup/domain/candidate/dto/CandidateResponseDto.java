package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CandidateResponseDto {
    private Long id;
    private String name;
    private String image;
    private Integer finalWinCount;
    private Integer winCount;
    private Integer matchCount;
    private Integer matchUpGameCount;
    private Long worldCupId;
}
