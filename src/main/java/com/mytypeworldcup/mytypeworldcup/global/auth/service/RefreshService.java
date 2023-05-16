package com.mytypeworldcup.mytypeworldcup.global.auth.service;

import com.mytypeworldcup.mytypeworldcup.global.auth.entity.RefreshToken;
import com.mytypeworldcup.mytypeworldcup.global.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String email, String refreshToken, Date expiryDate) {
        RefreshToken token = RefreshToken
                .builder()
                .email(email)
                .token(refreshToken)
                .expiryDate(expiryDate)
                .build();
        refreshTokenRepository.save(token);
    }


}
