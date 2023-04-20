package com.mytypeworldcup.mytypeworldcup.domain.member.service;

import com.mytypeworldcup.mytypeworldcup.domain.member.dao.MemberRepository;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.member.exception.MemberExceptionCode;
import com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.dto.ProviderType;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import com.mytypeworldcup.mytypeworldcup.global.util.CustomAuthorityUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@AllArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;

    public Member createMember(Member member) {
        verifyExistsEmail(member.getEmail());

        // password 암호화
        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        // UserRole 세팅
        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);

        return memberRepository.save(member);
    }

    public Member findMember(String email, ProviderType providerType) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            if (optionalMember.get().getProviderType() == providerType) {
                return optionalMember.get();
            } else {
                throw new BusinessLogicException(MemberExceptionCode.경고합니다);
            }
        } else {
            Member member = Member.builder().email(email)
                    .nickname("잠시테스트")
                    .password("oauth") //TODO 방법 생각해내라
                    .providerType(providerType)
                    .roles(List.of("ROLE_USER")) //TODO 관련 로직 만드세요!!
                    .build();
            return memberRepository.save(member);
        }
    }

    @Transactional(readOnly = true)
    private void verifyExistsEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            throw new BusinessLogicException(MemberExceptionCode.MEMBER_EXISTS);
        }
    }
}
