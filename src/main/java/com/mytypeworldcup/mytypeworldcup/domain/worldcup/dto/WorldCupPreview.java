package com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WorldCupPreview {
    private Long id;
    private String title;
    private String description;
    private Boolean visibility;
    private Integer candidatesCount;

    @Builder
    public WorldCupPreview(Long id,
                           String title,
                           String description,
                           Boolean visibility,
                           Integer candidatesCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.visibility = visibility;
        this.candidatesCount = candidatesCount;
    }
}
