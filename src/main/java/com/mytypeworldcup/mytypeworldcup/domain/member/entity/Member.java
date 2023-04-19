package com.mytypeworldcup.mytypeworldcup.domain.member.entity;

import com.mytypeworldcup.mytypeworldcup.global.common.Auditable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    private String nickname;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Builder
    public Member(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}