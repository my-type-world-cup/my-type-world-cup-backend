package com.mytypeworldcup.mytypeworldcup.domain.member.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberPatchDto {
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{1,16}$", message = "닉네임은 영문, 숫자, 한글만 사용하여 1글자 이상 16자 이내만 가능합니다.")
    private String nickname;

    @Builder
    public MemberPatchDto(String nickname) {
        this.nickname = nickname;
    }
}
