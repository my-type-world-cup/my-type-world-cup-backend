package com.mytypeworldcup.mytypeworldcup.global.common;

import com.mytypeworldcup.mytypeworldcup.global.common.NaverItemResponseDto;
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