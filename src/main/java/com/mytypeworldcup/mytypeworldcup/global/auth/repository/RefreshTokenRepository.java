package com.mytypeworldcup.mytypeworldcup.global.auth.repository;

import com.mytypeworldcup.mytypeworldcup.global.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
