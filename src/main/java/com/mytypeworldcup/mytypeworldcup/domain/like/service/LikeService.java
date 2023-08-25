package com.mytypeworldcup.mytypeworldcup.domain.like.service;

import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;

public interface LikeService {

    void createLike(Member member, Comment comment);

    void deleteLike(Member member, Comment comment);

}
