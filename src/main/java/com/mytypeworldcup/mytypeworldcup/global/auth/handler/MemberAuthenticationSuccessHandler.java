package com.mytypeworldcup.mytypeworldcup.global.auth.handler;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.global.auth.jwt.JwtTokenizer;
import com.mytypeworldcup.mytypeworldcup.global.util.CustomAuthorityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
public class MemberAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        // 인증 성공 후, 토큰 생성
        // Todo 리프레쉬 토큰 관련 고민할 것

        Member member;

        try {
            member = (Member) authentication.getPrincipal();
        } catch (ClassCastException e) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = (String) oAuth2User.getAttributes().get("email");
            member = emailToMember(email);
        }

        String accessToken = jwtTokenizer.delegateAccessToken(member);
        String refreshToken = jwtTokenizer.delegateRefreshToken(member);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh", refreshToken);
    }

    Member emailToMember(String email) {
        // 이메일과 역할 세팅
        // 닉네임 추가설정이 필요할 경우 클레임 수정
        return Member
                .builder()
                .email(email)
                .roles(authorityUtils.createRoles(email))
                .build();
    }
}