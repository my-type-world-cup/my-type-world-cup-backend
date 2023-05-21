package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CandidatePostDto {
    @NotBlank
    private String name;
    @NotBlank
    private String image;
    @NotBlank
    private String thumb;
    @NotNull
    private Long worldCupId;

    @Builder
    public CandidatePostDto(String name,
                            String image,
                            String thumb,
                            Long worldCupId) {
        this.name = name;
        this.image = image;
        this.thumb = thumb;
        this.worldCupId = worldCupId;
    }

    public WorldCup getWorldCup() {
        if (worldCupId == null) {
            return null;
        }

        WorldCup worldCup = new WorldCup();
        worldCup.setId(worldCupId);

        return worldCup;
    }

}
