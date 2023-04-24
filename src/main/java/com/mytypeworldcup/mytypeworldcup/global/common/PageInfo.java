package com.mytypeworldcup.mytypeworldcup.global.common;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PageInfo {
    private boolean first;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    @Builder
    public PageInfo(boolean first,
                    int page,
                    int size,
                    long totalElements,
                    int totalPages,
                    boolean last) {
        this.first = first;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }
}
