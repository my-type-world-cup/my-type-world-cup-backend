package com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WorldCupRequestDto {
    private Long worldCupId;
    private Integer teamCount;
    private String password;

    @Builder
    public WorldCupRequestDto(Long worldCupId, Integer teamCount, String password) {
        this.worldCupId = worldCupId;
        this.teamCount = teamCount;
        this.password = password;
    }
}
