package com.mytypeworldcup.mytypeworldcup.domain.comment.dto;

import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment commentPostDtoToComment(CommentPostDto commentPostDto);

    CommentResponseDto commentToCommentResponseDto(Comment comment);
}
