package com.mytypeworldcup.mytypeworldcup.domain.comment.repository;

import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    Page<CommentResponseDto> findAllByWorldCupId(Long worldCupId, Pageable pageable);
}
