package com.mytypeworldcup.mytypeworldcup.domain.like.repository;

import com.mytypeworldcup.mytypeworldcup.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByComment_IdAndMember_Id(Long commentId, Long memberId);
}
