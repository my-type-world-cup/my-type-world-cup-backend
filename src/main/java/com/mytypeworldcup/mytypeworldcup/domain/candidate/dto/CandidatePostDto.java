package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CandidatePostDto {

    private String name;

    private String image;
    @Setter
    private Long worldCupId;

    @Builder
    public CandidatePostDto(String name, String image) {
        this.name = name;
        this.image = image;
    }

}
