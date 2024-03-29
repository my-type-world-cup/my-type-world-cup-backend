package com.mytypeworldcup.mytypeworldcup.domain.comment.service;

import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentMapper;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.comment.exception.CommentExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.comment.repository.CommentRepository;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto createComment(CommentPostDto commentPostDto) {
        Comment comment = commentMapper.commentPostDtoToComment(commentPostDto);
        Comment savedComment = commentRepository.save(comment);
        CommentResponseDto commentResponseDto = commentMapper.commentToCommentResponseDto(savedComment);
        return commentResponseDto;
    }

    @Override
    public Page<CommentResponseDto> findCommentsByWorldCupId(Long worldCupId, Long memberId, Pageable pageable) {
        return commentRepository.findAllByWorldCupId(worldCupId, memberId, pageable);
    }

    @Override
    public Comment findVerifiedCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(CommentExceptionCode.COMMENT_NOT_FOUND));
    }

    public void test() {

    }
}
