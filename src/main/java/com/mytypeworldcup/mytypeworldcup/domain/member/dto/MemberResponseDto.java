package com.mytypeworldcup.mytypeworldcup.domain.member.dto;

import com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.dto.ProviderType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private ProviderType providerType;

    @Builder
    public MemberResponseDto(Long id,
                             String email,
                             String nickname,
                             ProviderType providerType) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.providerType = providerType;
    }
}
