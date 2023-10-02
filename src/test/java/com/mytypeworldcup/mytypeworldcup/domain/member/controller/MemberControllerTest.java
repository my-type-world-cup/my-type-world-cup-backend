package com.mytypeworldcup.mytypeworldcup.domain.member.controller;

import com.google.gson.Gson;
import com.mytypeworldcup.mytypeworldcup.domain.member.dto.MemberPatchDto;
import com.mytypeworldcup.mytypeworldcup.domain.member.dto.MemberResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.dto.ProviderType;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "secure-a-server.dolpick.com", uriPort = 0)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("로그인한 유저 멤버 정보 가져오기")
    void getLoginMember() throws Exception {
        // given
        MemberResponseDto response = MemberResponseDto.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .providerType(ProviderType.GOOGLE)
                .build();

        given(memberService.findMemberByEmail(anyString())).willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
                get("/members")
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "getLoginMember",
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 인증 토큰")
                        ),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("member 식별자"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("providerType").type(JsonFieldType.STRING).description("회원가입 방법")
                        )
                ));
    }

    @Test
    @DisplayName("멤버 정보 수정하기")
    void patchMember() throws Exception {
        // given
        MemberPatchDto request = MemberPatchDto.builder()
                .nickname("patchNickname")
                .build();

        MemberResponseDto response = MemberResponseDto.builder()
                .id(1L)
                .email("test@test.com")
                .nickname(request.getNickname())
                .providerType(ProviderType.GOOGLE)
                .build();

        given(memberService.updateMember(anyString(), any(MemberPatchDto.class))).willReturn(response);

        String content = gson.toJson(request);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/members")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "patchMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 인증 토큰")
                        ),
                        // 리퀘스트 바디
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("수정할 닉네임").optional()
                                        .attributes(key("constraints").value("length : 1이상 16이하" +
                                                " +\n" +
                                                "영문, 숫자, 한글"))),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("member 식별자"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("providerType").type(JsonFieldType.STRING).description("회원가입 방법")
                        )
                ));
    }
}