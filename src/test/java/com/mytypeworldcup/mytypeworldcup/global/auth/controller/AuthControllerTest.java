package com.mytypeworldcup.mytypeworldcup.global.auth.controller;

import com.mytypeworldcup.mytypeworldcup.global.auth.service.RefreshService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "secure-a-server.dolpick.com", uriPort = 0)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RefreshService refreshService;

    @Test
    @DisplayName("리프레쉬 - 액세스 토큰 재발급")
    void refresh() throws Exception {
        // given
        Cookie refreshTokenCookie = new Cookie("RefreshToken", "RefreshTokenCookie");
        String accessToken = "NewAccessToken";

        given(refreshService.refreshAccessToken(any())).willReturn(accessToken);

        // when
        ResultActions actions = mockMvc.perform(
                get("/auth/refresh")
                        .cookie(refreshTokenCookie)
                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "refresh",
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 쿠키
                        requestCookies(cookieWithName("RefreshToken").description("리프레쉬 토큰")),
                        // 리스폰스 바디
                        responseFields(fieldWithPath("data").type(JsonFieldType.STRING).description("액세스 토큰"))
                ));
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        // given
        Cookie refreshTokenCookie = new Cookie("RefreshToken", "RefreshTokenCookie");
        String accessToken = "NewAccessToken";

        given(refreshService.refreshAccessToken(any())).willReturn(accessToken);

        // when
        ResultActions actions = mockMvc.perform(
                delete("/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isNoContent())
                .andDo(document(
                        "logout",
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰"))
                ));
    }
}