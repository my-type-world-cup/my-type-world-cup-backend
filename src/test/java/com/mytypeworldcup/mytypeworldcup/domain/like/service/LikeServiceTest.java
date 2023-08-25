package com.mytypeworldcup.mytypeworldcup.domain.like.service;

import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.like.entity.Like;
import com.mytypeworldcup.mytypeworldcup.domain.like.exception.LikeExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.like.repository.LikeRepository;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    @InjectMocks
    private LikeServiceImpl likeService;
    @Mock
    private LikeRepository likeRepository;

    @Test
    @DisplayName("좋아요 생성 - 정상통과")
    void createLike_happy() {
        // given
        Member member = new Member();
        member.setId(1L);

        Comment comment = new Comment();
        comment.setId(2L);

        Like like = Like.builder().member(member).comment(comment).build();

        given(likeRepository.findByMemberAndComment(any(Member.class), any(Comment.class))).willReturn(Optional.ofNullable(null));
        given(likeRepository.save(any(Like.class))).willReturn(like);

        // when
        likeService.createLike(member, comment);

        // then
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    @DisplayName("좋아요 생성 - 이미좋아요를 누른경우")
    void createLike_bad() {
        Member member = new Member();
        member.setId(1L);

        Comment comment = new Comment();
        comment.setId(2L);

        Like like = Like.builder().member(member).comment(comment).build();

        given(likeRepository.findByMemberAndComment(any(Member.class), any(Comment.class))).willReturn(Optional.ofNullable(like));

        // when
        // then
        BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> likeService.createLike(member, comment));
        assertEquals(LikeExceptionCode.LIKE_EXISTS, thrown.getExceptionCode());
    }

    @Test
    @DisplayName("좋아요 삭제 - 정상통과")
    void deleteLike_happy() {
        // given
        Member member = new Member();
        member.setId(1L);

        Comment comment = new Comment();
        comment.setId(2L);

        Like like = Like.builder().member(member).comment(comment).build();

        given(likeRepository.findByMemberAndComment(any(Member.class), any(Comment.class))).willReturn(Optional.ofNullable(like));

        // when
        assertDoesNotThrow(() -> likeService.deleteLike(member, comment));

        // then
        verify(likeRepository).delete(like);
    }

    @Test
    @DisplayName("좋아요 삭제 - 좋아요 없음")
    void deleteLike_LIKE_NOT_FOUND() {
        // given
        Member member = new Member();
        member.setId(1L);

        Comment comment = new Comment();
        comment.setId(2L);

        given(likeRepository.findByMemberAndComment(any(Member.class), any(Comment.class))).willReturn(Optional.ofNullable(null));

        // when
        // then
        BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> likeService.deleteLike(member, comment));
        assertEquals(LikeExceptionCode.LIKE_NOT_FOUND, thrown.getExceptionCode());
        verify(likeRepository, never()).delete(any(Like.class));
    }
}