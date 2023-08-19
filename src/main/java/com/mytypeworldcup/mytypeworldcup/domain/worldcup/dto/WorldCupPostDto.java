package com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto;

import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
public class WorldCupPostDto {

    @Setter
    private Long memberId;

    @NotNull
    @Length(min = 1, max = 50)
    private String title; // 제목이므로 공백 불가능

    @Length(max = 200)
    private String description; // 생략 가능

    @Pattern(regexp = "\\d{4}", message = "비밀번호는 4자리의 숫자로 이루어져야 합니다.")
    private String password;

    @Builder
    public WorldCupPostDto(Long memberId,
                           String title,
                           String description,
                           String password) {
        this.memberId = memberId;
        this.title = title;
        this.description = description;
        this.password = password;
    }

    public Member getMember() {
        if (memberId == null) {
            return null;
        }

        Member member = new Member();
        member.setId(memberId);

        return member;
    }
}
