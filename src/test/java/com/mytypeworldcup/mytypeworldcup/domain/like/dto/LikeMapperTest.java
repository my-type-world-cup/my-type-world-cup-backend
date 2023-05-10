package com.mytypeworldcup.mytypeworldcup.domain.like.dto;

import com.mytypeworldcup.mytypeworldcup.domain.like.entity.Like;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class LikeMapperTest {
    @Spy
    private LikeMapper likeMapper = Mappers.getMapper(LikeMapper.class);

    @Test
    @DisplayName("LikePostDto -> Like")
    void likePostDtoToLike() {
        // given
        LikePostDto likePostDto = LikePostDto
                .builder()
                .commentId(1L)
                .build();
        likePostDto.setMemberId(2L);

        // when
        Like like = likeMapper.likePostDtoToLike(likePostDto);

        // then
        assertEquals(likePostDto.getCommentId(), like.getComment().getId());
        assertEquals(likePostDto.getMemberId(), like.getMember().getId());
    }
}