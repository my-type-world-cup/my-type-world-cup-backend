package com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WorldCupPatchDto {
    private String title;

    private String description;

    @Pattern(regexp = "(\\d{4})|^null$", message = "비밀번호는 4자리의 숫자로 이루어져야 하거나, String \"null\"을 입력해야 합니다.")
    private String password;

    @Builder
    public WorldCupPatchDto(String title,
                            String description,
                            String password) {
        this.title = title;
        this.description = description;
        this.password = password;
    }
}
