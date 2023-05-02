package com.mytypeworldcup.mytypeworldcup.domain.like.service;

import com.mytypeworldcup.mytypeworldcup.domain.like.dto.LikeMapper;
import com.mytypeworldcup.mytypeworldcup.domain.like.dto.LikePostDto;
import com.mytypeworldcup.mytypeworldcup.domain.like.entity.Like;
import com.mytypeworldcup.mytypeworldcup.domain.like.exception.LikeExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.like.repository.LikeRepository;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;

    public Long createLike(LikePostDto likePostDto) {
        verifyExistsLike(likePostDto.getCommentId(), likePostDto.getMemberId());
        Like like = likeMapper.likePostDtoToLike(likePostDto);
        Like savedLike = likeRepository.save(like);
        return savedLike.getId();
    }

    @Transactional(readOnly = true)
    private void verifyExistsLike(Long commentId, Long memberId) {
        Optional<Like> optionalLike = likeRepository.findByComment_IdAndMember_Id(commentId, memberId);
        if (optionalLike.isPresent()) {
            throw new BusinessLogicException(LikeExceptionCode.LIKE_EXISTS);
        }
    }

}
