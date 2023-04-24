package com.mytypeworldcup.mytypeworldcup.domain.member.controller;

import com.mytypeworldcup.mytypeworldcup.domain.member.dto.MemberDto;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.member.exception.MemberExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.dto.ProviderType;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    //    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity postMember(@RequestBody MemberDto.Post memberPostDto) {

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
    public ResponseEntity getMember(Authentication authentication) {

//        String name = authentication.getName();
//        System.out.println(userDetails.getUsername());
        System.out.println("겟멤버" + authentication);
        System.out.println(authentication.getPrincipal());

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/testEX")
    public ResponseEntity test() {

        throw new BusinessLogicException(MemberExceptionCode.MEMBER_NOT_FOUND);

//        return new ResponseEntity<>(HttpStatus.OK);
    }


//    @GetMapping("/hello-oauth2")
//    public ResponseEntity home(Authentication authentication) {
//        var authorizedClient = authorizedClientService.loadAuthorizedClient("google", authentication.getName());
//
//        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
//        System.out.println("accessToken.getTokenValue() = " + accessToken.getTokenValue());
//        System.out.println("accessToken.getTokenType().getValue() = " + accessToken.getTokenType().getValue());
//        System.out.println("accessToken.getScopes() = " + accessToken.getScopes());
//        System.out.println("accessToken.getIssuedAt() = " + accessToken.getIssuedAt());
//        System.out.println("accessToken.getExpiresAt() = " + accessToken.getExpiresAt());
//
//        return new ResponseEntity("hello-oauth2", HttpStatus.OK);
//    }

    @GetMapping("/v11/members")
    public ResponseEntity 모두사용가능() {
        return new ResponseEntity("모든 이용자가 접속가능합니다", HttpStatus.OK);
    }

    @GetMapping("/v11/users")
    public ResponseEntity 일반유저만가능() {
        return new ResponseEntity("유저권한만 가능", HttpStatus.OK);
    }

    @GetMapping("/v11/admin")
    public ResponseEntity 관리자만가능() {
        return new ResponseEntity("관리자만가능", HttpStatus.OK);
    }

    @GetMapping("/v11/loginUser")
    public ResponseEntity 로그인유저가능() {
        return new ResponseEntity("로그인유저만가능", HttpStatus.OK);
    }

    @PostMapping("/v11/members")
    public ResponseEntity postMember(@RequestBody Member member) {
//        memberService.createMember(member);
        return new ResponseEntity("회원가입성공", HttpStatus.CREATED);
    }
}
