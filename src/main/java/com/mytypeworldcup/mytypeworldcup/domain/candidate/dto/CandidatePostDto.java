package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CandidatePostDto {

    private String name;
    private String image;
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
