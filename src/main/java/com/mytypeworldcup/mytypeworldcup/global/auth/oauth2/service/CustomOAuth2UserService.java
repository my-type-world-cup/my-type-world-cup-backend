package com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.service;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.member.repository.MemberRepository;
import com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.dto.ProviderType;
import com.mytypeworldcup.mytypeworldcup.global.util.CustomAuthorityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils authorityUtils;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return this.process(userRequest, super.loadUser(userRequest));
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        // 서비스 구분을 위한 작업 (구글:google, 네이버: naver)
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 내가 필요한 정보들 선언
        String email;
        String name;

        Map<String, Object> response = oAuth2User.getAttributes();

        switch (providerType) {
//            case NAVER:
//                Map<String, Object> hash = (Map<String, Object>) response.get("response");
//                email = (String) hash.get("email");
//                break;

            case GOOGLE:
                email = (String) response.get("email");
                name = (String) response.get("name");
                break;

            default:
                throw new OAuth2AuthenticationException("허용되지 않는 인증입니다.");
        }

        // 이미 가입한 사람인지 확인
        Optional<Member> optionalUser = memberRepository.findByEmail(email);
        Member member;

        if (optionalUser.isPresent()) { // 이미 있는 사람이면
            member = optionalUser.get(); // member를 돌려준다.

//            if (member.getProviderType() == ProviderType.NATIVE) { // 가입한적은 있으나 네이티브로 가입했을 경우 에러발생
//                throw new BusinessLogicException(MemberExceptionCode.MEMBER_EXISTS); // Todo 네이티브로 회원가입한 회원이라고 알려주자
            // Todo 에러 발생시 GlobalExceptionAdvice가 못잡음 500에러 발생함
//            } 네이티브 로그인 회원가입 시 이메일 인증을 하도록하면 굳이 안해도 된다 ???????


        } else { // 가입한적 없는 사람이면
            member = Member.builder()
                    .email(email)
                    .nickname(name)
                    .password("oauth") //TODO 방법 생각해내라
                    .providerType(providerType)
                    .roles(authorityUtils.createRoles(email))
                    .build();

            memberRepository.save(member);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoles().toString())),
                oAuth2User.getAttributes(),
                userNameAttributeName
        );
    }
}
