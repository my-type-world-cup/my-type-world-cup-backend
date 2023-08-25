package com.mytypeworldcup.mytypeworldcup.domain.member.service;

import com.mytypeworldcup.mytypeworldcup.domain.member.dto.MemberPatchDto;
import com.mytypeworldcup.mytypeworldcup.domain.member.dto.MemberResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;

public interface MemberService {

    Member createMember(Member member);

    //    Member findMember(String email, ProviderType providerType);
    MemberResponseDto updateMember(String email, MemberPatchDto memberPatchDto);

    MemberResponseDto findMemberByEmail(String email);

    Long findMemberIdByEmail(String email);

    Member findVerifiedMemberByEmail(String email);

}
