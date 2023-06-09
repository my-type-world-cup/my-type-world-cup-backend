package com.mytypeworldcup.mytypeworldcup.global.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public class SearchRequestParamDto {
    @Min(0)
    private int page;

    @Positive
    private int size;

    private String sort;

    private Sort.Direction direction;

    private String keyword;

    public SearchRequestParamDto(Integer page,
                                 Integer size,
                                 String sort,
                                 Sort.Direction direction,
                                 String keyword) {
        this.page = page == null ? 0 : page - 1;
        this.size = size == null ? 5 : size;
        this.sort = sort == null ? "createdAt" : sort;
        this.direction = direction == null ? Sort.Direction.DESC : direction;
        this.keyword = keyword;
    }
}
