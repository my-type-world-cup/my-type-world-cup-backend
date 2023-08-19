package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class CandidatePatchDto {
    @Length(min = 1, max = 50)
    private String name;
    private String image;
    private String thumb;

    @Builder
    public CandidatePatchDto(String name,
                             String image,
                             String thumb) {
        this.name = name;
        this.image = image;
        this.thumb = thumb;
    }
}
