package com.mytypeworldcup.mytypeworldcup.domain.comment.service;

import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentMapper;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;

    @Test
    @DisplayName("댓글 달기 - 비회원 댓글")
    void createComment_anonymous() {
        // given
        LocalDateTime localDateTime = LocalDateTime.now();

        CommentPostDto commentPostDto = CommentPostDto
                .builder()
                .content("카리나가 짱이지!")
                .candidateName("카리나")
                .worldCupId(1L)
                .build();

        CommentResponseDto expected = CommentResponseDto
                .builder()
                .id(1L)
                .content(commentPostDto.getContent())
                .candidateName(commentPostDto.getCandidateName())
                .likesCount(0)
                .createdAt(localDateTime)
                .modifiedAt(localDateTime)
                .memberId(null)
                .nickname(null)
                .worldCupId(commentPostDto.getWorldCupId())
                .build();

        given(commentMapper.commentPostDtoToComment(any(CommentPostDto.class))).willReturn(new Comment());
        given(commentRepository.save(any(Comment.class))).willReturn(new Comment());
        given(commentMapper.commentToCommentResponseDto(any(Comment.class))).willReturn(expected);

        // when
        CommentResponseDto actual = commentService.createComment(commentPostDto);

        // then
        assertSame(expected, actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getContent(), actual.getContent());
        assertEquals(expected.getCandidateName(), actual.getCandidateName());
        assertEquals(expected.getLikesCount(), actual.getLikesCount());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getModifiedAt(), actual.getModifiedAt());
        assertEquals(expected.getMemberId(), actual.getMemberId());
        assertEquals(expected.getNickname(), actual.getNickname());
        assertEquals(expected.getWorldCupId(), actual.getWorldCupId());
    }

    @Test
    @DisplayName("댓글 달기 - 회원 댓글")
    void createComment_member() {
        // given
        LocalDateTime localDateTime = LocalDateTime.now();

        CommentPostDto commentPostDto = CommentPostDto
                .builder()
                .content("카리나가 짱이지!")
                .candidateName("카리나")
                .worldCupId(1L)
                .build();
        commentPostDto.setMemberId(1L);

        CommentResponseDto expected = CommentResponseDto
                .builder()
                .id(1L)
                .content(commentPostDto.getContent())
                .candidateName(commentPostDto.getCandidateName())
                .likesCount(0)
                .createdAt(localDateTime)
                .modifiedAt(localDateTime)
                .memberId(commentPostDto.getMemberId())
                .nickname("시영")
                .worldCupId(commentPostDto.getWorldCupId())
                .build();

        given(commentMapper.commentPostDtoToComment(any(CommentPostDto.class))).willReturn(new Comment());
        given(commentRepository.save(any(Comment.class))).willReturn(new Comment());
        given(commentMapper.commentToCommentResponseDto(any(Comment.class))).willReturn(expected);

        // when
        CommentResponseDto actual = commentService.createComment(commentPostDto);

        // then
        assertSame(expected, actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getContent(), actual.getContent());
        assertEquals(expected.getCandidateName(), actual.getCandidateName());
        assertEquals(expected.getLikesCount(), actual.getLikesCount());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getModifiedAt(), actual.getModifiedAt());
        assertEquals(expected.getMemberId(), actual.getMemberId());
        assertEquals(expected.getNickname(), actual.getNickname());
        assertEquals(expected.getWorldCupId(), actual.getWorldCupId());
    }

    @Test
    void findCommentsByWorldCupId() {
    }
}