package com.mytypeworldcup.mytypeworldcup.global.auth.repository;

import com.mytypeworldcup.mytypeworldcup.global.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String refreshToken);
}
