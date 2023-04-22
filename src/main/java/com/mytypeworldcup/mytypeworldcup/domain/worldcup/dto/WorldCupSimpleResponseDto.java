package com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class WorldCupSimpleResponseDto {
    private Long id;
    private String title;
    private String description;
    @Setter
    private List<CandidateSimpleResponseDto> candidateSimpleResponseDtos;

    @QueryProjection
    public WorldCupSimpleResponseDto(Long id,
                                     String title,
                                     String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
}
