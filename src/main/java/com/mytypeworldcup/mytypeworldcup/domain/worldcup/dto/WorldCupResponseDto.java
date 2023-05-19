package com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class WorldCupResponseDto {
    private Long id;
    private String title;
    private String description;
    private String password;
    private Long memberId;
    private List<CandidateResponseDto> candidateResponseDtos;

    @Builder
    public WorldCupResponseDto(Long id,
                               String title,
                               String description,
                               String password,
                               Long memberId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.password = password;
        this.memberId = memberId;
    }

    public void setCandidateResponseDtos(List<CandidateResponseDto> candidateResponseDtos) {
        this.candidateResponseDtos = candidateResponseDtos;
    }

}
