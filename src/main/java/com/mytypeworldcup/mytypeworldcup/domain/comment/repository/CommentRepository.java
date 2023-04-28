package com.mytypeworldcup.mytypeworldcup.domain.comment.repository;

import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
}
