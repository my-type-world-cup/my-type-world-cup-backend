package com.mytypeworldcup.mytypeworldcup.global.auth.service;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.global.auth.entity.RefreshToken;
import com.mytypeworldcup.mytypeworldcup.global.auth.exception.AuthExceptionCode;
import com.mytypeworldcup.mytypeworldcup.global.auth.jwt.JwtTokenizer;
import com.mytypeworldcup.mytypeworldcup.global.auth.repository.RefreshTokenRepository;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import com.mytypeworldcup.mytypeworldcup.global.util.CustomAuthorityUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

import static com.mytypeworldcup.mytypeworldcup.global.auth.utils.CookieUtil.deleteCookie;

@Service
//@Transactional
@RequiredArgsConstructor
public class RefreshService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    @Transactional
    public void saveRefreshToken(String email, String refreshToken, Date expiryDate) {
        RefreshToken token = RefreshToken
                .builder()
                .email(email)
                .token(refreshToken)
                .expiryDate(expiryDate)
                .build();
        refreshTokenRepository.save(token);
    }

    public String refreshAccessToken(Cookie refreshTokenCookie, HttpServletResponse response) {
        // 리프레쉬토큰을 찾는다
        // 존재하지않으면 예외발생
        RefreshToken refreshToken = findVerifiedRefreshToken(refreshTokenCookie.getValue());

        // 유효기간확인
        // 유효기간 만료됬을경우 디비에서 토큰삭제 및 브라우저 토큰 삭제
        if (refreshToken.getExpiryDate().toInstant().isBefore(Instant.now())) { // 토큰의 만료일과 현재시간을 비교
            refreshTokenRepository.delete(refreshToken);
            deleteCookie(refreshTokenCookie, response);
            // 로그인 유도
            throw new BusinessLogicException(AuthExceptionCode.REFRESH_TOKEN_EXPIRED);
        }

        // 새로운 액세스토큰생성 및 리턴
        Member member = Member.builder()
                .email(refreshToken.getEmail())
                .roles(authorityUtils.createRoles(refreshToken.getEmail()))
                .build();
        String accessToken = jwtTokenizer.delegateAccessToken(member);

        return accessToken;
    }

    @Transactional
    public void logout(String email) {
        RefreshToken refreshToken = findVerifiedRefreshTokenByEmail(email);
        refreshTokenRepository.delete(refreshToken);
    }

    @Transactional(readOnly = true)
    private RefreshToken findVerifiedRefreshTokenByEmail(String email) {
        return refreshTokenRepository.findById(email)
                .orElseThrow(() -> new BusinessLogicException(AuthExceptionCode.REFRESH_TOKEN_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    private RefreshToken findVerifiedRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessLogicException(AuthExceptionCode.REFRESH_TOKEN_NOT_FOUND));
    }

}
