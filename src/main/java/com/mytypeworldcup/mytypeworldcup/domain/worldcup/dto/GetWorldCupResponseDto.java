package com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetWorldCupResponseDto {
    /**
     * getWorldCup() 메서드의 리스폰스 디티오 입니다
     */
    private Long id;
    private String title;
    private String description;
    private Boolean visibility;
    private Integer candidatesCount;

    @Builder
    public GetWorldCupResponseDto(Long id,
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
