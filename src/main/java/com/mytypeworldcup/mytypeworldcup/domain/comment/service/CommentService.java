package com.mytypeworldcup.mytypeworldcup.domain.comment.service;

import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponseDto createComment(CommentPostDto commentPostDto);

    Page<CommentResponseDto> findCommentsByWorldCupId(Long worldCupId, Long memberId, Pageable pageable);

    Comment findVerifiedCommentById(Long id);

}
