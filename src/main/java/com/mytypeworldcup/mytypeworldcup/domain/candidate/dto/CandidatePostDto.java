package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class CandidatePostDto {
    @NotNull
    @Length(min = 1, max = 50)
    private String name;
    @NotNull
    @Length(min = 1, max = 255)
    private String image;
    @NotNull
    @Length(min = 1, max = 255)
    private String thumb;
    @Positive
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
