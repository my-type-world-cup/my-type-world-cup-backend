package com.mytypeworldcup.mytypeworldcup.domain.member.service;

import com.mytypeworldcup.mytypeworldcup.domain.member.dao.MemberRepository;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.global.security.CustomAuthorityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@AllArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils authorityUtils;

    // 멤버가 존재하면 null, 존재하지않으면 생성
    public Member createMember(Member member) {

        Optional<Member> optionalMember = existsEmail(member.getEmail());
        if (optionalMember.isPresent()) {
            return optionalMember.get();

        } else {
            List<String> roles = authorityUtils.createRoles(member.getEmail());
            member.setRoles(roles);
            return memberRepository.save(member);
        }
    }

    private Optional<Member> existsEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
