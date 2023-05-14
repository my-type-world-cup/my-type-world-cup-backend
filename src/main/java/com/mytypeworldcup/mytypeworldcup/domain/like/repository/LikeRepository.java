package com.mytypeworldcup.mytypeworldcup.domain.like.repository;

import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.like.entity.Like;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByMemberAndComment(Member member, Comment comment);
}
