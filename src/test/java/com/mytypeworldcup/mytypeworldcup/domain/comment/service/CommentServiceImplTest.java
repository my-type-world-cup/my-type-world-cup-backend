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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;

    @Test
    @DisplayName("댓글 달기 - 비회원 댓글")
    void createComment_anonymous() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2023, 05, 10, 00, 51, 15);

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
        LocalDateTime localDateTime = LocalDateTime.of(2023, 05, 10, 00, 51, 15);

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
    @DisplayName("댓글 목록 보기")
    void findCommentsByWorldCupId() {
        // given
        Pageable pageable = PageRequest.of(0, 5, DESC, "likesCount");
        Long worldCupId = 1L;
        LocalDateTime localDateTime = LocalDateTime.of(2023, 05, 10, 00, 51, 15);

        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            CommentResponseDto commentResponseDto = CommentResponseDto
                    .builder()
                    .id(i)
                    .content("윈터가 짱이지! - " + i)
                    .candidateName("윈터")
                    .likesCount((int) (100 - i))
                    .isLiked(false)
                    .createdAt(localDateTime)
                    .modifiedAt(localDateTime)
                    .memberId(null)
                    .nickname(null)
                    .worldCupId(worldCupId)
                    .build();
            commentResponseDtos.add(commentResponseDto);
        }

        Page<CommentResponseDto> expected = new PageImpl<>(commentResponseDtos, pageable, 30);

        given(commentRepository.findAllByWorldCupId(anyLong(), any(), any(Pageable.class))).willReturn(expected);

        // when
        Page<CommentResponseDto> actual = commentService.findCommentsByWorldCupId(worldCupId, null, pageable);

        // then
        assertSame(expected, actual);
        assertArrayEquals(expected.getContent().toArray(), actual.getContent().toArray());
        assertEquals(expected.getPageable(), actual.getPageable());
    }
}