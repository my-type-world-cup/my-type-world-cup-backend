package com.mytypeworldcup.mytypeworldcup.global.common;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponseDto<T> {

    private List<T> data;
    private PageInfo pageInfo;

    public PageResponseDto(Page<T> data) {
        this.data = data.getContent();
        this.pageInfo = PageInfo
                .builder()
                .first(data.isFirst())
                .page(data.getNumber() + 1) // 0부터 시작
                .size(data.getSize())
                .totalElements(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .last(data.isLast())
                .build();
    }

}
