package com.mytypeworldcup.mytypeworldcup.domain.member.controller;

import com.mytypeworldcup.mytypeworldcup.domain.member.dto.MemberPatchDto;
import com.mytypeworldcup.mytypeworldcup.domain.member.dto.MemberPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.member.dto.MemberResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.dto.ProviderType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity postMember(@RequestBody @Valid MemberPostDto memberPostDto) {

        Member member = Member
                .builder()
                .email(memberPostDto.getEmail())
                .password(memberPostDto.getPassword())
                .nickname(memberPostDto.getNickname())
                .providerType(ProviderType.NATIVE)
                .build();

        memberService.createMember(member);

        return new ResponseEntity("회원가입이 완료되었습니다", HttpStatus.CREATED);
    }

    @GetMapping("/members")
    public ResponseEntity getLoginMember(Authentication authentication) {
        MemberResponseDto memberResponseDto = memberService.findMemberByEmail(authentication.getName());
        return ResponseEntity.ok(memberResponseDto);
    }

    @PatchMapping("/members")
    public ResponseEntity patchMember(Authentication authentication,
                                      @RequestBody @Valid MemberPatchDto memberPatchDto) {
        MemberResponseDto memberResponseDto = memberService.updateMember(authentication.getName(), memberPatchDto);
        return ResponseEntity.ok(memberResponseDto);
    }
}
