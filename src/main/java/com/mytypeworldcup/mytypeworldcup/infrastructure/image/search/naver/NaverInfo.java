package com.mytypeworldcup.mytypeworldcup.infrastructure.image.search.naver;

import lombok.Getter;

import java.util.List;

@Getter
public class NaverInfo {
    private String lastBuildDate; // Date
    private Integer total;
    private Integer start;

    private Integer display;
    private List<NaverItem> items;
}