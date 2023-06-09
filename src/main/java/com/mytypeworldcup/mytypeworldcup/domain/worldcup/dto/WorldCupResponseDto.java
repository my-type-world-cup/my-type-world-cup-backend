package com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WorldCupResponseDto {
    private Long id;
    private String title;
    private String description;
    private String password;
    private Long memberId;

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
}
