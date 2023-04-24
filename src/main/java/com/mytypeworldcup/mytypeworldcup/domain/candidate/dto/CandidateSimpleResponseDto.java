package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CandidateSimpleResponseDto {
    private Long id;
    private String name;
    private String image;

    @QueryProjection
    public CandidateSimpleResponseDto(Long id,
                                      String name,
                                      String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }
}
