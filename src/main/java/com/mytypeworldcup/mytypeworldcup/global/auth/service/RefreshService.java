package com.mytypeworldcup.mytypeworldcup.global.auth.service;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.global.auth.entity.RefreshToken;
import com.mytypeworldcup.mytypeworldcup.global.auth.exception.AuthExceptionCode;
import com.mytypeworldcup.mytypeworldcup.global.auth.jwt.JwtTokenizer;
import com.mytypeworldcup.mytypeworldcup.global.auth.repository.RefreshTokenRepository;
import com.mytypeworldcup.mytypeworldcup.global.auth.utils.CookieUtil;
import com.mytypeworldcup.mytypeworldcup.global.auth.utils.HeaderUtil;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
//@Transactional
@RequiredArgsConstructor
public class RefreshService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenizer jwtTokenizer;

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

    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = HeaderUtil.getAccessToken(request);
        String refreshToken = CookieUtil.getCookie(request, "RefreshToken")
                .getValue();

        Claims claims = jwtTokenizer.extractClaimsFromAccessToken(accessToken); // 기존 AccessToken 에서 email 추출
        String email = claims.getSubject();
        RefreshToken verifiedRefreshToken = findVerifiedRefreshTokenByEmail(email); // email 기반으로 refreshToken 찾아오기

        if (!verifiedRefreshToken.getToken().equals(refreshToken)) { // 사용자의 토큰과 DB 의 토큰을 비교
            // Todo: 인증되지 않은 사용자가 접근하려함. 경고 메일 보내기
            CookieUtil.deleteCookie(request, response, "RefreshToken");
            refreshTokenRepository.delete(verifiedRefreshToken); // 토큰 유출가능성 있으므로 삭제
            throw new BusinessLogicException(AuthExceptionCode.UNAUTHORIZED);
        }

        if (verifiedRefreshToken.getExpiryDate().toInstant().isBefore(Instant.now())) { // 토큰의 만료일과 현재시간을 비교
            CookieUtil.deleteCookie(request, response, "RefreshToken");
            refreshTokenRepository.delete(verifiedRefreshToken);
            throw new BusinessLogicException(AuthExceptionCode.REFRESH_TOKEN_EXPIRED);
        }

        response.addHeader("Authorization", jwtTokenizer.delegateAccessToken(claimsToMember(claims)));
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

    private Member claimsToMember(Claims claims) {
        return Member.builder()
                .email(claims.getSubject())
                .roles((List<String>) claims.get("roles"))
                .build();
    }

}
