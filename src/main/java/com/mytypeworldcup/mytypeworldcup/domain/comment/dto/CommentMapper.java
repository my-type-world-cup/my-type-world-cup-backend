package com.mytypeworldcup.mytypeworldcup.domain.comment.dto;

import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment commentPostDtoToComment(CommentPostDto commentPostDto);

    /**
     * 댓글 생성시에만 사용할 것. 이외에 사용하려면 getIsLiked 해결할 것
     */
    CommentResponseDto commentToCommentResponseDto(Comment comment);
}
