package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CandidatePostDto {
    @NotBlank
    private String name;
    @NotBlank
    private String image;
    @NotBlank
    private String thumb;
    @Setter
    private Long worldCupId;

    @Builder
    public CandidatePostDto(String name,
                            String image,
                            String thumb) {
        this.name = name;
        this.image = image;
        this.thumb = thumb;
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
