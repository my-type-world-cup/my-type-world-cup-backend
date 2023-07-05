package com.mytypeworldcup.mytypeworldcup.global.auth.handler;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.global.auth.jwt.JwtTokenizer;
import com.mytypeworldcup.mytypeworldcup.global.auth.service.RefreshService;
import com.mytypeworldcup.mytypeworldcup.global.util.CustomAuthorityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static com.mytypeworldcup.mytypeworldcup.global.auth.utils.CookieUtil.addHttpOnlyCookie;

@RequiredArgsConstructor
public class MemberAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;
    private final RefreshService refreshService;
    private final String clientUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 인증 성공 후, 토큰 생성
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

        // 쿠키 설정
//        addCookie("AccessToken", accessToken, "localhost", jwtTokenizer.getAccessTokenExpirationSeconds(), response);
        addHttpOnlyCookie("RefreshToken", refreshToken, request.getServerName(), jwtTokenizer.getRefreshTokenExpirationSeconds(), response);

        // RefreshToken 저장
        refreshService.saveRefreshToken(member.getEmail(), refreshToken, jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationSeconds()));

        // 리다이렉트 URI 설정
        getRedirectStrategy().sendRedirect(request, response, createURI(accessToken));
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

    private String createURI(String accessToken) {
        return UriComponentsBuilder
                .fromUriString(clientUrl)
                .path("callback")
                .queryParam("access_token", accessToken)
                .build()
                .toUriString();
    }
}