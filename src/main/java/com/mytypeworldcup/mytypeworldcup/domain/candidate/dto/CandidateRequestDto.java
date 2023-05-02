package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CandidateRequestDto {
    @NotNull
    private Long worldCupId;
    private String password;

    @Builder
    public CandidateRequestDto(Long worldCupId,
                               String password) {
        this.worldCupId = worldCupId;
        this.password = password;
    }
}
