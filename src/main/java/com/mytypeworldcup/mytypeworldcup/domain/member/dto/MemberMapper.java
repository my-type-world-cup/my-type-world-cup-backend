package com.mytypeworldcup.mytypeworldcup.domain.member.dto;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberResponseDto memberToMemberResponseDto(Member member);

}
