package com.mytypeworldcup.mytypeworldcup.domain.like.dto;

import com.mytypeworldcup.mytypeworldcup.domain.like.entity.Like;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    Like likePostDtoToLike(LikePostDto likePostDto);
}
