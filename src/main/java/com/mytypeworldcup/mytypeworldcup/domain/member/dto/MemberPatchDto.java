package com.mytypeworldcup.mytypeworldcup.domain.member.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberPatchDto {
    @Pattern(regexp = "^(?=.*[a-zA-Z0-9가-힣])[a-zA-Z0-9가-힣]{1,16}$", message = "닉네임은 영문1, 숫자1, 한글2로 계산하여 총 16자로 구성되어야 합니다.")
    private String nickname;

    @Builder
    public MemberPatchDto(String nickname) {
        this.nickname = nickname;
    }
}
