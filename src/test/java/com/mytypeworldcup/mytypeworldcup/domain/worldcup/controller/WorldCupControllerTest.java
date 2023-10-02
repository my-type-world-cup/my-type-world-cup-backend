package com.mytypeworldcup.mytypeworldcup.domain.worldcup.controller;

import com.google.gson.Gson;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.*;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import com.mytypeworldcup.mytypeworldcup.global.common.PageResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorldCupController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "secure-a-server.dolpick.com", uriPort = 0)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser(roles = {"USER", "ADMIN"})
public class WorldCupControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @MockBean
    private WorldCupService worldCupService;
    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("월드컵 등록")
    void postWorldCup() throws Exception {
        // given
        Long memberId = 1L;
        Long worldCupId = 1L;

        WorldCupPostDto request = WorldCupPostDto
                .builder()
                .title("테스트 월드컵의 제목입니다.")
                .description("테스트 월드컵의 설명입니다.")
                .password("1234")
                .build();

        WorldCupResponseDto response = WorldCupResponseDto
                .builder()
                .id(worldCupId)
                .title(request.getTitle())
                .description(request.getDescription())
                .password(request.getPassword())
                .memberId(memberId)
                .build();

        given(memberService.findMemberIdByEmail(anyString())).willReturn(memberId);
        given(worldCupService.createWorldCup(any(WorldCupPostDto.class))).willReturn(response);

        String content = gson.toJson(request);

        // when
        ResultActions actions = mockMvc.perform(
                post("/worldcups")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(worldCupId))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.password").value(request.getPassword()))
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andDo(document(
                        "postWorldCup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 인증 토큰")
                        ),
                        // 리퀘스트 바디
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("worldCup 제목")
                                        .attributes(key("constraints").value("length : 1이상 50이하")),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("worldCup 설명").optional()
                                        .attributes(key("constraints").value("length : 200이하")),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("worldCup 암호").optional()
                                        .attributes(key("constraints").value("4자리 숫자"))
                        ),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("worldCup 식별자"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("worldCup 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("worldCup 설명"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("worldCup 암호"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("member 식별자")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("특정 월드컵 정보 수정")
    void patchWorldCup() throws Exception {
        // given
        long worldCupId = 1L;
        long memberId = 2L;
        WorldCupPatchDto request = WorldCupPatchDto.builder()
                .title("제목 수정하기")
                .description("설명 수정하기")
                .password("1423")
                .build();

        WorldCupResponseDto response = WorldCupResponseDto.builder()
                .id(worldCupId)
                .title(request.getTitle())
                .description(request.getDescription())
                .password(request.getPassword())
                .memberId(memberId)
                .build();

        doNothing().when(worldCupService).verifyWorldCupAccess(anyString(), anyLong());
        given(worldCupService.updateWorldCup(anyLong(), any(WorldCupPatchDto.class))).willReturn(response);

        String content = gson.toJson(request);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/worldcups/{worldCupId}", worldCupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(worldCupId))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.password").value(request.getPassword()))
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andDo(document(
                        "patchWorldCup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 인증 토큰")
                        ),
                        // 패스 파라미터
                        pathParameters(
                                parameterWithName("worldCupId").description("worldCup 식별자")
                                        .attributes(key("constraints").value("1이상"))),
                        // 리퀘스트 바디
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("worldCup 제목")
                                        .attributes(key("constraints").value("length : 1이상 50이하")),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("worldCup 설명").optional()
                                        .attributes(key("constraints").value("length : 200이하")),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("worldCup 암호").optional()
                                        .attributes(key("constraints").value("4자리 숫자" +
                                                " +\n" +
                                                "암호 제거 시 `\"null\"` 입력"))
                        ),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("worldCup 식별자"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("worldCup 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("worldCup 설명"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("worldCup 암호"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("member 식별자")
                        )
                ))
        ;
    }

    @DisplayName("월드컵 검색 - 메인페이지")
    @Test
    void getWorldCups() throws Exception {
        // given
        String page = "1";
        String size = "5";
        String sort = "playCount";
        String direction = "DESC";
        String keyword = "월드컵";

        List<WorldCupSimpleResponseDto> data = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            WorldCupSimpleResponseDto dummy = new WorldCupSimpleResponseDto(i, "월드컵 " + i, "설명입니다.");
            dummy.setCandidateSimpleResponseDtos(List.of(
                    new CandidateSimpleResponseDto(i, "테스트 이미지 " + i, "테스트 이미지 url", "테스트 썸네일 url"),
                    new CandidateSimpleResponseDto(i + 3, "테스트 이미지 " + i + 3, "테스트 이미지 url", "테스트 썸네일 url")));
            data.add(dummy);
        }

        Page<WorldCupSimpleResponseDto> responseDtos = new PageImpl<>(data);

        given(worldCupService.searchWorldCups(isNull(), anyString(), any(Pageable.class))).willReturn(responseDtos);

        // when
        ResultActions actions = mockMvc.perform(
                get("/worldcups")
                        .param("page", page)
                        .param("size", size)
                        .param("sort", sort)
                        .param("direction", direction)
                        .param("keyword", keyword)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "getWorldCups",
                        preprocessResponse(prettyPrint()),
                        // 쿼리 파라미터
                        queryParameters(
                                parameterWithName("page").description("요청 페이지").optional()
                                        .attributes(
                                                key("default").value("1"),
                                                key("constraints").value("1이상")
                                        ),
                                parameterWithName("size").description("페이지 당 데이터 수").optional()
                                        .attributes(
                                                key("default").value("5"),
                                                key("constraints").value("1이상")
                                        ),
                                parameterWithName("sort").description("정렬 기준").optional()
                                        .attributes(
                                                key("default").value("createdAt(미입력 시)," +
                                                        " +\n" +
                                                        "playCount(제약조건 외 값 입력 시)"),
                                                key("constraints").value("createdAt(생성일시), commentCount(댓글수), playCount(플레이횟수)")
                                        ),
                                parameterWithName("direction").description("정렬 방법").optional()
                                        .attributes(
                                                key("default").value("DESC"),
                                                key("constraints").value("ASC 또는 DESC")
                                        ),
                                parameterWithName("keyword").description("검색어 (title, description 에서 검색)").optional()
                                        .attributes(
//                                                key("default").value(""),
//                                                key("constraints").value("ASC 또는 DESC")
                                        )
                        ),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("pageInfo.first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지 여부"),
                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 당 게시물 수"),
                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("총 게시물 수"),
                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("pageInfo.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("data[*].id").type(JsonFieldType.NUMBER).description("worldCup 식별자"),
                                fieldWithPath("data[*].title").type(JsonFieldType.STRING).description("worldCup 제목"),
                                fieldWithPath("data[*].description").type(JsonFieldType.STRING).description("worldCup 설명"),
                                fieldWithPath("data[*].candidateSimpleResponseDtos[*].id").type(JsonFieldType.NUMBER).description("candidate 식별자"),
                                fieldWithPath("data[*].candidateSimpleResponseDtos[*].name").type(JsonFieldType.STRING).description("candidate 이름"),
                                fieldWithPath("data[*].candidateSimpleResponseDtos[*].image").type(JsonFieldType.STRING).description("candidate 이미지 주소"),
                                fieldWithPath("data[*].candidateSimpleResponseDtos[*].thumb").type(JsonFieldType.STRING).description("candidate 썸네일 주소")
                        )
                ))
        ;

        String actual = actions.andReturn().getResponse().getContentAsString();
        String expected = gson.toJson(new PageResponseDto(responseDtos));

        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("특정 월드컵 미리보기")
    @Test
    void getWorldCupPreview() throws Exception {
        // given
        long worldCupId = 1L;
        WorldCupPreview response = WorldCupPreview
                .builder()
                .id(worldCupId)
                .title("테스트 타이틀")
                .description("테스트 설명")
                .visibility(true)
                .candidatesCount(10)
                .build();

        given(worldCupService.findWorldCupPreview(anyLong())).willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
                get("/worldcups/{worldCupId}", worldCupId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "getWorldCupPreview",
                        preprocessResponse(prettyPrint()),
                        // 패스 파라미터
                        pathParameters(
                                parameterWithName("worldCupId").description("worldCup 식별자")
                                        .attributes(key("constraints").value("1이상"))),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("worldCup 식별자"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("worldCup 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("worldCup 설명"),
                                fieldWithPath("visibility").type(JsonFieldType.BOOLEAN).description("공개 여부"),
                                fieldWithPath("candidatesCount").type(JsonFieldType.NUMBER).description("candidate 숫자")
                        )
                ))
        ;

        String expected = gson.toJson(response);
        String actual = actions.andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(expected, actual);

    }

    @Test
    @DisplayName("특정 월드컵 상세 정보 보기")
    void getWorldCupDetails() throws Exception {
        // given
        long worldCupId = 1L;
        long memberId = 2L;
        WorldCupResponseDto response = WorldCupResponseDto.builder()
                .id(worldCupId)
                .title("테스트 월드컵 제목")
                .description("테스트 월드컵 설명")
                .password("1423")
                .memberId(memberId)
                .build();

        doNothing().when(worldCupService).verifyWorldCupAccess(anyString(), anyLong());
        given(worldCupService.findWorldCupDetails(anyLong())).willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
                get("/worldcups/{worldCupId}/details", worldCupId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(worldCupId))
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andExpect(jsonPath("$.description").value(response.getDescription()))
                .andExpect(jsonPath("$.password").value(response.getPassword()))
                .andExpect(jsonPath("$.memberId").value(response.getMemberId()))
                .andDo(document(
                        "getWorldCupDetails",
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 인증 토큰")
                        ),
                        // 패스 파라미터
                        pathParameters(
                                parameterWithName("worldCupId").description("worldCup 식별자")
                                        .attributes(key("constraints").value("1이상"))),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("worldCup 식별자"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("worldCup 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("worldCup 설명"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("worldCup 암호"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("worldCup 생성자 식별자")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("나의 월드컵 보기")
    void getMyWorldCups() throws Exception {
        // given
        String page = "1";
        String size = "5";
        String sort = "playCount";
        String direction = "DESC";
        String keyword = "월드컵";
        Long memberId = 10L;

        List<WorldCupSimpleResponseDto> data = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            WorldCupSimpleResponseDto dummy = new WorldCupSimpleResponseDto(i, "월드컵 " + i, "설명입니다.");
            dummy.setCandidateSimpleResponseDtos(List.of(
                    new CandidateSimpleResponseDto(i, "테스트 이미지 " + i, "테스트 이미지 url", "테스트 썸네일 url"),
                    new CandidateSimpleResponseDto(i + 3, "테스트 이미지 " + i + 3, "테스트 이미지 url", "테스트 썸네일 url")));
            data.add(dummy);
        }

        Page<WorldCupSimpleResponseDto> responseDtos = new PageImpl<>(data);

        given(memberService.findMemberIdByEmail(anyString())).willReturn(memberId);
        given(worldCupService.searchWorldCups(anyLong(), anyString(), any(Pageable.class))).willReturn(responseDtos);

        // when
        ResultActions actions = mockMvc.perform(
                get("/members/worldcups")
                        .param("page", page)
                        .param("size", size)
                        .param("sort", sort)
                        .param("direction", direction)
                        .param("keyword", keyword)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "getMyWorldCups",
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 인증 토큰")
                        ),
                        // 쿼리 파라미터
                        queryParameters(
                                parameterWithName("page").description("요청 페이지").optional()
                                        .attributes(
                                                key("default").value("1"),
                                                key("constraints").value("1이상")
                                        ),
                                parameterWithName("size").description("페이지 당 데이터 수").optional()
                                        .attributes(
                                                key("default").value("5"),
                                                key("constraints").value("1이상")
                                        ),
                                parameterWithName("sort").description("정렬 기준").optional()
                                        .attributes(
                                                key("default").value("createdAt(미입력 시)," +
                                                        " +\n" +
                                                        "playCount(제약조건 외 값 입력 시)"),
                                                key("constraints").value("createdAt, commentCount, playCount")
                                        ),
                                parameterWithName("direction").description("정렬 방법").optional()
                                        .attributes(
                                                key("default").value("DESC"),
                                                key("constraints").value("ASC 또는 DESC")
                                        ),
                                parameterWithName("keyword").description("검색어 (title, description 에서 검색)").optional()
                                        .attributes(
//                                                key("default").value(""),
//                                                key("constraints").value("ASC 또는 DESC")
                                        )
                        ),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("pageInfo.first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지 여부"),
                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 당 게시물 수"),
                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("총 게시물 수"),
                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("pageInfo.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("data[*].id").type(JsonFieldType.NUMBER).description("worldCup 식별자"),
                                fieldWithPath("data[*].title").type(JsonFieldType.STRING).description("worldCup 제목"),
                                fieldWithPath("data[*].description").type(JsonFieldType.STRING).description("worldCup 설명"),
                                fieldWithPath("data[*].candidateSimpleResponseDtos[*].id").type(JsonFieldType.NUMBER).description("candidate 식별자"),
                                fieldWithPath("data[*].candidateSimpleResponseDtos[*].name").type(JsonFieldType.STRING).description("candidate 이름"),
                                fieldWithPath("data[*].candidateSimpleResponseDtos[*].image").type(JsonFieldType.STRING).description("candidate 이미지 주소"),
                                fieldWithPath("data[*].candidateSimpleResponseDtos[*].thumb").type(JsonFieldType.STRING).description("candidate 썸네일 주소")
                        )
                ))
        ;

        verify(memberService).findMemberIdByEmail(anyString());
        verify(worldCupService).searchWorldCups(anyLong(), anyString(), any(Pageable.class));

        String actual = actions.andReturn().getResponse().getContentAsString();
        String expected = gson.toJson(new PageResponseDto(responseDtos));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("특정 월드컵 삭제")
    void deleteWorldCup() throws Exception {
        // given
        long worldCupId = 1L;
        doNothing().when(worldCupService).verifyWorldCupAccess(anyString(), anyLong());
        doNothing().when(worldCupService).deleteWorldCup(anyLong());

        // when
        ResultActions actions = mockMvc.perform(
                delete("/worldcups/{worldCupId}", worldCupId)
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isNoContent())
                .andDo(document(
                        "deleteWorldCup",
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 인증 토큰")
                        ),
                        // 패스 파라미터
                        pathParameters(
                                parameterWithName("worldCupId").description("worldCup 식별자")
                                        .attributes(key("constraints").value("1이상")))
                ))
        ;
    }
}
