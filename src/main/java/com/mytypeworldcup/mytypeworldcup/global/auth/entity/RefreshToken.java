package com.mytypeworldcup.mytypeworldcup.global.auth.entity;

import com.mytypeworldcup.mytypeworldcup.global.common.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
public class RefreshToken extends Auditable {
    @Id
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String token;

    private Date expiryDate;

    @Builder
    public RefreshToken(String email,
                        String token,
                        Date expiryDate) {
        this.email = email;
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
