package com.mytypeworldcup.mytypeworldcup.global.config;

import com.mytypeworldcup.mytypeworldcup.global.auth.handler.MemberAccessDeniedHandler;
import com.mytypeworldcup.mytypeworldcup.global.auth.handler.MemberAuthenticationFailureHandler;
import com.mytypeworldcup.mytypeworldcup.global.auth.handler.MemberAuthenticationSuccessHandler;
import com.mytypeworldcup.mytypeworldcup.global.auth.jwt.JwtAuthenticationFilter;
import com.mytypeworldcup.mytypeworldcup.global.auth.jwt.JwtTokenizer;
import com.mytypeworldcup.mytypeworldcup.global.auth.jwt.JwtVerificationFilter;
import com.mytypeworldcup.mytypeworldcup.global.auth.jwt.MemberAuthenticationEntryPoint;
import com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.service.CustomOAuth2UserService;
import com.mytypeworldcup.mytypeworldcup.global.auth.service.RefreshService;
import com.mytypeworldcup.mytypeworldcup.global.util.CustomAuthorityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;
    private final CustomOAuth2UserService oAuth2UserService;
    private final RefreshService refreshService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .frameOptions().sameOrigin() //동일 출처로부터 들어오는 request만 페이지 렌더링 허용 h2
                )
                .csrf().disable() // csrf 공격에 대한 설정 비활성화
                .cors(withDefaults()) // cors설정 -> withDefaults() 일경우 corsConfigurationSource 라는 이름으로 등록된 Bean을 이용함
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 스테이트리스
                .and()
                .formLogin().disable() // 폼로그인 방식 비활성화 -> SSR 방식에서 사용
                .httpBasic().disable() // Username/Password 정보를 HTTP헤더에 실어서 인증하는 방식 비활성화
                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .and()
                .apply(new CustomFilterConfigurer())
                .and()

                // -> Security Filter(UsernamePasswordAuthenticationFilter, BasicAuthenticationFilter 등) 비활성화됨

                .authorizeHttpRequests(
                        authorize -> authorize
                                // Member
                                .requestMatchers(HttpMethod.GET, "/members").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/members").authenticated()

                                // Auth
                                .requestMatchers(HttpMethod.DELETE, "/auth/logout").authenticated()

                                // WorldCup
                                .requestMatchers(HttpMethod.POST, "/worldcups").authenticated()
                                .requestMatchers(HttpMethod.GET, "/members/worldcups").authenticated()
                                .requestMatchers(HttpMethod.GET, "/worldcups/*/details").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/worldcups/*").authenticated()

                                // Candidate
                                .requestMatchers(HttpMethod.POST, "/candidates").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/candidates/*").authenticated()

                                // Comment
                                .requestMatchers(HttpMethod.POST, "/comments").permitAll()

                                // Like
                                .requestMatchers(HttpMethod.POST, "/comments/*/likes").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/comments/*/likes").authenticated()

                                // images
                                .requestMatchers(HttpMethod.GET, "/images").authenticated()

                                .anyRequest().permitAll()
                )


//                .requestMatchers("/").permitAll()
//                .requestMatchers("/login").permitAll()
//                .requestMatchers("/user").hasRole("USER")
//                .anyRequest().authenticated()
//                .and()
//                .exceptionHandling().accessDeniedPage("/accessDenied")
//                .and()
//                .logout().logoutUrl("/logout")
//                .logoutSuccessUrl("/").permitAll()
//                .and()
//
                // OAuth2.0 로그인 설정
                .oauth2Login()
//                .defaultSuccessUrl(redirectUrl) // 임시 리다이렉트
                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()
                .successHandler(new MemberAuthenticationSuccessHandler(jwtTokenizer, authorityUtils, refreshService))
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * .cors(withDefaults()) 시 사용됨
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();   // corsConfigurationSource 인터페이스의 구현 클래스인 UrlBasedCorsConfigurationSource 클래스의 객체를 생성

//        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("*"));   // 모든 출처(Origin)에 대해 스크립트 기반의 HTTP 통신을 허용하도록 설정 -> TODO 운영환경에 맞게 변경할것
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));  // setAllowedMethods()를 통해 파라미터로 지정한 HTTP Method에 대한 HTTP 통신을 허용
        configuration.addAllowedHeader("*");

        source.registerCorsConfiguration("/**", configuration);      // 모든 URL에 앞에서 구성한 CORS 정책(CorsConfiguration)을 적용
        return source;
    }

    // JwtAuthenticationFilter를 등록하는 역할
    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer);
            jwtAuthenticationFilter.setFilterProcessesUrl("/login/native");

            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler(jwtTokenizer, authorityUtils, refreshService));
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils);

            builder.addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }
}
