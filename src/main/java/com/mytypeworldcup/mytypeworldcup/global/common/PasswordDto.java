package com.mytypeworldcup.mytypeworldcup.global.common;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordDto {
    @Pattern(regexp = "\\d{4}", message = "비밀번호는 4자리의 숫자로 이루어져야 합니다.")
    private String password;

    public PasswordDto(String password) {
        this.password = password;
    }
}
