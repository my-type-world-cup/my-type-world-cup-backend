package com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RandomWorldCupRequestDto {
    private Long worldCupId;
    private Integer teamCount;
    private String password;

    @Builder
    public RandomWorldCupRequestDto(Long worldCupId,
                                    Integer teamCount,
                                    String password) {
        this.worldCupId = worldCupId;
        this.teamCount = teamCount;
        this.password = password;
    }
}
