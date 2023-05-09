package com.mytypeworldcup.mytypeworldcup.domain.like.service;

import com.mytypeworldcup.mytypeworldcup.domain.like.dto.LikeMapper;
import com.mytypeworldcup.mytypeworldcup.domain.like.dto.LikePostDto;
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

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @InjectMocks
    private LikeService likeService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeMapper likeMapper;


    @Test
    @DisplayName("좋아요 생성 - 정상통과")
    void createLike_happy() throws IllegalAccessException, NoSuchFieldException {
        // given
        LikePostDto likePostDto = LikePostDto
                .builder()
                .commentId(1L)
                .build();
        likePostDto.setMemberId(3L);

        Like like = Like
                .builder()
                .comment(likePostDto.getComment())
                .member(likePostDto.getMember())
                .build();

        // Like id 임시로 변경
        Field idField = like.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(like, 1L);

        given(likeRepository.findByComment_IdAndMember_Id(anyLong(), anyLong())).willReturn(Optional.ofNullable(null));
        given(likeMapper.likePostDtoToLike(any(LikePostDto.class))).willReturn(like);
        given(likeRepository.save(any(Like.class))).willReturn(like);

        // when
        Long actual = likeService.createLike(likePostDto);

        // then
        assertEquals(1L, actual);
        assertEquals(likePostDto.getCommentId(), like.getComment().getId());
        assertEquals(likePostDto.getMemberId(), like.getMember().getId());
    }

    @Test
    @DisplayName("좋아요 생성 - 이미좋아요를 누른경우")
    void createLike_bad() {
        // given
        LikePostDto likePostDto = LikePostDto
                .builder()
                .commentId(1L)
                .build();
        likePostDto.setMemberId(3L);

        given(likeRepository.findByComment_IdAndMember_Id(anyLong(), anyLong())).willReturn(Optional.ofNullable(new Like()));

        // when
        // then
        BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> likeService.createLike(likePostDto));
        assertEquals(LikeExceptionCode.LIKE_EXISTS, thrown.getExceptionCode());
    }

    @Test
    @DisplayName("좋아요 삭제 - 정상통과")
    void deleteLike_happy() {
        // given
        Long memberId = 3L;
        Long likeId = 1L;

        Member member = new Member();
        member.setId(memberId);

        Like like = Like
                .builder()
                .member(member)
                .build();

        given(likeRepository.findById(anyLong())).willReturn(Optional.ofNullable(like));

        // when
        assertDoesNotThrow(() -> likeService.deleteLike(memberId, likeId));

        // then
        verify(likeRepository).delete(like);
    }

    @Test
    @DisplayName("좋아요 삭제 - 권한없음")
    void deleteLike_FORBIDDEN() {
        // given
        Long memberId = 1L;
        Long likeId = 10L;

        Member member = new Member();
        member.setId(2L); // 다른 멤버의 ID

        Like like = Like
                .builder()
                .member(member)
                .build();

        given(likeRepository.findById(likeId)).willReturn(Optional.ofNullable(like));

        // when
        // then
        BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> likeService.deleteLike(memberId, likeId));
        assertEquals(LikeExceptionCode.FORBIDDEN, thrown.getExceptionCode());
        verify(likeRepository, never()).delete(like);
    }

    @Test
    @DisplayName("좋아요 삭제 - 좋아요 없음")
    void deleteLike_LIKE_NOT_FOUND() {
        // given
        Long memberId = 1L;
        Long likeId = 10L;

        given(likeRepository.findById(likeId)).willReturn(Optional.ofNullable(null));

        // when
        // then
        BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> likeService.deleteLike(memberId, likeId));
        assertEquals(LikeExceptionCode.LIKE_NOT_FOUND, thrown.getExceptionCode());
        verify(likeRepository, never()).delete(any(Like.class));
    }
}