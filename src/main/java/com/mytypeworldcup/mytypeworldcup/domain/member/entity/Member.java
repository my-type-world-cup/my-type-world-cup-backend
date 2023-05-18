package com.mytypeworldcup.mytypeworldcup.domain.member.entity;

import com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.dto.ProviderType;
import com.mytypeworldcup.mytypeworldcup.global.common.Auditable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 60)
    private String nickname;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderType providerType;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List<String> roles = new ArrayList<>();

    @Builder
    public Member(String email, String nickname, String password, ProviderType providerType, List<String> roles) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.providerType = providerType;
        this.roles = roles;
    }
}