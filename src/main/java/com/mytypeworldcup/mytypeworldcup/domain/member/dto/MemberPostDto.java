package com.mytypeworldcup.mytypeworldcup.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberPostDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank(message = "이름은 공백이 아니어야 합니다.")
    private String nickname;

    @Builder
    public MemberPostDto(String email,
                         String password,
                         String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}