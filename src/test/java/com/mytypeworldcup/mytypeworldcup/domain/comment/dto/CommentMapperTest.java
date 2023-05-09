package com.mytypeworldcup.mytypeworldcup.domain.comment.dto;

import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.like.entity.Like;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import com.mytypeworldcup.mytypeworldcup.global.common.Auditable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    @DisplayName("CommentPostDto -> Comment - 익명유저")
    void commentPostDtoToComment_anonymous() {
        // given
        CommentPostDto commentPostDto = CommentPostDto
                .builder()
                .content("테스트 댓글입니다.")
                .candidateName("테스트후보")
                .worldCupId(1L)
                .build();

        // when
        Comment comment = commentMapper.commentPostDtoToComment(commentPostDto);

        // then
        assertEquals(commentPostDto.getContent(), comment.getContent());
        assertEquals(commentPostDto.getCandidateName(), comment.getCandidateName());
        assertEquals(commentPostDto.getWorldCupId(), comment.getWorldCupId());
        assertNull(comment.getMember());
    }

    @Test
    @DisplayName("CommentPostDto -> Comment - 회원")
    void commentPostDtoToComment_member() {
        // given
        CommentPostDto commentPostDto = CommentPostDto
                .builder()
                .content("테스트 댓글입니다.")
                .candidateName("테스트후보")
                .worldCupId(1L)
                .build();
        commentPostDto.setMemberId(2L);

        // when
        Comment comment = commentMapper.commentPostDtoToComment(commentPostDto);

        // then
        assertEquals(commentPostDto.getContent(), comment.getContent());
        assertEquals(commentPostDto.getCandidateName(), comment.getCandidateName());
        assertEquals(commentPostDto.getWorldCupId(), comment.getWorldCupId());
        assertEquals(commentPostDto.getMemberId(), comment.getMemberId());
    }

    @Test
    @DisplayName("Comment -> CommentResponseDto")
    void commentToCommentResponseDto() throws NoSuchFieldException, IllegalAccessException {
        // given
        Member member = Member
                .builder()
                .nickname("테스트닉네임")
                .build();
        member.setId(2L);

        WorldCup worldCup = new WorldCup();
        worldCup.setId(1L);

        Comment comment = Comment
                .builder()
                .content("테스트 댓글입니다")
                .candidateName("테스트후보")
                .member(member)
                .worldCup(worldCup)
                .build();

        for (int i = 0; i < 5; i++) {
            comment.getLikes().add(new Like());
        }

        // comment id, createdAt, modifiedAt 설정
        Field idField = Comment.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(comment, 3L);

        Field createdAtField = Auditable.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(comment, LocalDateTime.of(2023, 5, 10, 00, 35));

        Field modifiedAtField = Auditable.class.getDeclaredField("modifiedAt");
        modifiedAtField.setAccessible(true);
        modifiedAtField.set(comment, LocalDateTime.of(2023, 5, 10, 00, 42));

        // when
        CommentResponseDto commentResponseDto = commentMapper.commentToCommentResponseDto(comment);

        // then
        assertEquals(comment.getId(), commentResponseDto.getId());
        assertEquals(comment.getContent(), commentResponseDto.getContent());
        assertEquals(comment.getCandidateName(), commentResponseDto.getCandidateName());
        assertEquals(comment.getLikesCount(), commentResponseDto.getLikesCount());
        assertEquals(comment.getCreatedAt(), commentResponseDto.getCreatedAt());
        assertEquals(comment.getModifiedAt(), commentResponseDto.getModifiedAt());
        assertEquals(comment.getMemberId(), commentResponseDto.getMemberId());
        assertEquals(comment.getNickname(), commentResponseDto.getNickname());
        assertEquals(comment.getWorldCupId(), commentResponseDto.getWorldCupId());
    }
}