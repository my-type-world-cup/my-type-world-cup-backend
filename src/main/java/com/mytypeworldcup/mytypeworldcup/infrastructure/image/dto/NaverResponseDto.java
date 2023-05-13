package com.mytypeworldcup.mytypeworldcup.infrastructure.image.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class NaverResponseDto {
    private String lastBuildDate; // Date
    private Integer total;
    private Integer start;

    private Integer display;
    private List<NaverItemResponseDto> items;
}