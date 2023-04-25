package com.mytypeworldcup.mytypeworldcup.global.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytypeworldcup.mytypeworldcup.domain.member.dto.LoginDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;

    // 인증
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        ObjectMapper objectMapper = new ObjectMapper(); // 역직렬화
        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

        // Username 과 Password 를 포함한 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);  // 인증 위임
    }

    // 클라이언트의 인증 정보를 이용해 인증에 성공할 경우 호출됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws ServletException, IOException {
        /** 토큰을 생성하는 일을 MemberAuthenticationSuccessHandler 에게 위임 */
//        Member member = (Member) authResult.getPrincipal();  // Member 엔티티 클래스의 객체를 얻음

//        String accessToken = jwtTokenizer.delegateAccessToken(member);   // 액세스토큰생성
//        String refreshToken = jwtTokenizer.delegateRefreshToken(member); // 리프레쉬 토큰 생성

//        response.setHeader("Authorization", "Bearer " + accessToken);
//        response.setHeader("Refresh", refreshToken);

//        System.out.println("jwt어센티케이션필터 인증성공!!!");

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult); //MemberAuthenticationSuccessHandler 로 이동
    }
}