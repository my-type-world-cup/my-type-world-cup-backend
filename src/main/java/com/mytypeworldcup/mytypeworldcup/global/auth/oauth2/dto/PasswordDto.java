package com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordDto {
    private String password;

    public PasswordDto(String password) {
        this.password = password;
    }
}
