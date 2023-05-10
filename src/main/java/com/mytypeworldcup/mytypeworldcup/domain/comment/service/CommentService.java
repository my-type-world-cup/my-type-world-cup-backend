package com.mytypeworldcup.mytypeworldcup.domain.comment.service;

import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentMapper;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentResponseDto createComment(CommentPostDto commentPostDto) {
        Comment comment = commentMapper.commentPostDtoToComment(commentPostDto);
        Comment savedComment = commentRepository.save(comment);
        CommentResponseDto commentResponseDto = commentMapper.commentToCommentResponseDto(savedComment);
        return commentResponseDto;
    }

    public Page<CommentResponseDto> findCommentsByWorldCupId(Long worldCupId, Long memberId, Pageable pageable) {
        return commentRepository.findAllByWorldCupId(worldCupId, memberId, pageable);
    }

}
