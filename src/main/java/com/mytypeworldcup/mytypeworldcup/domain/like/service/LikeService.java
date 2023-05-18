package com.mytypeworldcup.mytypeworldcup.domain.like.service;

import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.like.entity.Like;
import com.mytypeworldcup.mytypeworldcup.domain.like.exception.LikeExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.like.repository.LikeRepository;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
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

    public void createLike(Member member, Comment comment) {
        verifyExistsLike(member, comment);
        likeRepository.save(Like.builder().member(member).comment(comment).build());
    }

    public void deleteLike(Member member, Comment comment) {
        Like like = findVerifiedLike(member, comment);
        likeRepository.delete(like);
    }

    @Transactional(readOnly = true)
    private void verifyExistsLike(Member member, Comment comment) {
        Optional<Like> optionalLike = likeRepository.findByMemberAndComment(member, comment);
        if (optionalLike.isPresent()) {
            throw new BusinessLogicException(LikeExceptionCode.LIKE_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    private Like findVerifiedLike(Member member, Comment comment) {
        return likeRepository.findByMemberAndComment(member, comment)
                .orElseThrow(() -> new BusinessLogicException(LikeExceptionCode.LIKE_NOT_FOUND));
    }

}
