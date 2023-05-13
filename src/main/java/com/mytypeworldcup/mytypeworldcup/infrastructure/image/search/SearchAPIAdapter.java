package com.mytypeworldcup.mytypeworldcup.infrastructure.image.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchAPIAdapter {
    Page<String> searchImages(String keyword, Pageable pageable);
}
