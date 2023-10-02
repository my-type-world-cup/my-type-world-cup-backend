package com.mytypeworldcup.mytypeworldcup.domain.candidate.controller;

import com.google.gson.Gson;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.*;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.service.CandidateService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import com.mytypeworldcup.mytypeworldcup.global.common.PageResponseDto;
import com.mytypeworldcup.mytypeworldcup.global.common.PasswordDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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

@WebMvcTest(CandidateController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "secure-a-server.dolpick.com", uriPort = 0)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser
class CandidateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @MockBean
    private WorldCupService worldCupService;
    @MockBean
    private CandidateService candidateService;

    @Test
    @DisplayName("후보 등록")
    void postCandidate() throws Exception {
        // given
        CandidatePostDto request = CandidatePostDto.builder()
                .name("테스트")
                .image("image Url")
                .thumb("thumb Url")
                .worldCupId(1L)
                .build();

        CandidateResponseDto response = CandidateResponseDto.builder()
                .id(3L)
                .name(request.getName())
                .image(request.getImage())
                .thumb(request.getThumb())
                .finalWinCount(0)
                .winCount(0)
                .matchUpWorldCupCount(0)
                .matchUpGameCount(0)
                .worldCupId(request.getWorldCupId())
                .build();

        doNothing().when(worldCupService).verifyWorldCupAccess(anyString(), anyLong());
        given(candidateService.createCandidate(any(CandidatePostDto.class))).willReturn(response);

        String content = gson.toJson(request);

        // when
        ResultActions actions = mockMvc.perform(
                post("/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.image").value(response.getImage()))
                .andExpect(jsonPath("$.finalWinCount").value(0))
                .andExpect(jsonPath("$.winCount").value(0))
                .andExpect(jsonPath("$.matchUpWorldCupCount").value(0))
                .andExpect(jsonPath("$.matchUpGameCount").value(0))
                .andExpect(jsonPath("$.worldCupId").value(response.getWorldCupId()))
                .andDo(document(
                        "postCandidate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        // 리퀘스트 바디
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("candidate 이름")
                                        .attributes(key("constraints").value("length : 1이상 50이하")),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 주소")
                                        .attributes(key("constraints").value("length : 1이상 255이하")),
                                fieldWithPath("thumb").type(JsonFieldType.STRING).description("썸네일 이미지 주소")
                                        .attributes(key("constraints").value("length : 1이상 255이하")),
                                fieldWithPath("worldCupId").type(JsonFieldType.NUMBER).description("worldCup 식별자")
                                        .attributes(key("constraints").value("1이상"))
                        ),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("candidate 식별자"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("candidate 이름"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 주소"),
                                fieldWithPath("thumb").type(JsonFieldType.STRING).description("썸네일 이미지 주소"),
                                fieldWithPath("finalWinCount").type(JsonFieldType.NUMBER).description("최종 우승 횟수"),
                                fieldWithPath("winCount").type(JsonFieldType.NUMBER).description("1대1 승리 횟수"),
                                fieldWithPath("matchUpWorldCupCount").type(JsonFieldType.NUMBER).description("월드컵 참가 횟수"),
                                fieldWithPath("matchUpGameCount").type(JsonFieldType.NUMBER).description("1대1 매칭 횟수"),
                                fieldWithPath("worldCupId").type(JsonFieldType.NUMBER).description("worldCup 식별자")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("후보 정보 수정")
    void patchCandidate() throws Exception {
        // given
        long candidateId = 1L;
        CandidatePatchDto request = CandidatePatchDto.builder()
                .name("테스트")
                .image("image Url")
                .thumb("thumb Url")
                .build();

        CandidateResponseDto response = CandidateResponseDto.builder()
                .id(candidateId)
                .name(request.getName())
                .image(request.getImage())
                .thumb(request.getThumb())
                .finalWinCount(0)
                .winCount(0)
                .matchUpWorldCupCount(0)
                .matchUpGameCount(0)
                .worldCupId(1L)
                .build();

        doNothing().when(candidateService).verifyAccess(anyString(), anyLong());
        given(candidateService.updateCandidate(anyLong(), any(CandidatePatchDto.class))).willReturn(response);

        String content = gson.toJson(request);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/candidates/{candidateId}", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.image").value(response.getImage()))
                .andExpect(jsonPath("$.finalWinCount").value(0))
                .andExpect(jsonPath("$.winCount").value(0))
                .andExpect(jsonPath("$.matchUpWorldCupCount").value(0))
                .andExpect(jsonPath("$.matchUpGameCount").value(0))
                .andExpect(jsonPath("$.worldCupId").value(response.getWorldCupId()))
                .andDo(document(
                        "patchCandidate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        // 패스 파라미터
                        pathParameters(
                                parameterWithName("candidateId").description("candidate 식별자")
                                        .attributes(key("constraints").value("1이상"))),
                        // 리퀘스트 바디
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("candidate 이름").optional()
                                        .attributes(key("constraints").value("length : 1이상 50이하")),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 주소").optional()
                                        .attributes(key("constraints").value("length : 1이상 255이하")),
                                fieldWithPath("thumb").type(JsonFieldType.STRING).description("썸네일 이미지 주소").optional()
                                        .attributes(key("constraints").value("length : 1이상 255이하"))
                        ),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("candidate 식별자"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("candidate 이름"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 주소"),
                                fieldWithPath("thumb").type(JsonFieldType.STRING).description("썸네일 이미지 주소"),
                                fieldWithPath("finalWinCount").type(JsonFieldType.NUMBER).description("최종 우승 횟수"),
                                fieldWithPath("winCount").type(JsonFieldType.NUMBER).description("1대1 승리 횟수"),
                                fieldWithPath("matchUpWorldCupCount").type(JsonFieldType.NUMBER).description("월드컵 참가 횟수"),
                                fieldWithPath("matchUpGameCount").type(JsonFieldType.NUMBER).description("1대1 매칭 횟수"),
                                fieldWithPath("worldCupId").type(JsonFieldType.NUMBER).description("worldCup 식별자")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("월드컵 결과 반영")
    void patchMatchResults() throws Exception {
        // given
        List<MatchDto> patchDtos = new ArrayList<>();
        for (long i = 1; i <= 4; i++) {
            MatchDto matchDto = MatchDto
                    .builder()
                    .id(1L)
                    .matchUpGameCount(4)
                    .winCount(1)
                    .build();
            patchDtos.add(matchDto);
        }

        String content = gson.toJson(patchDtos);

        doNothing().when(candidateService).updateMatchResults(anyList());

        // when
        ResultActions actions = mockMvc.perform(
                patch("/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedCandidates").value(patchDtos.size()))
                .andDo(document(
                        "patchMatchResults",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 바디
                        requestFields(
                                fieldWithPath("[*].id").type(JsonFieldType.NUMBER).description("candidate 식별자")
                                        .attributes(key("constraints").value("1이상")),
                                fieldWithPath("[*].matchUpGameCount").type(JsonFieldType.NUMBER).description("1대1 매칭 횟수")
                                        .attributes(key("constraints").value("1이상")),
                                fieldWithPath("[*].winCount").type(JsonFieldType.NUMBER).description("1대1 승리 횟수")
                                        .attributes(key("constraints").value("0이상"))
                        ),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("updatedCandidates").type(JsonFieldType.NUMBER).description("업데이트 된 candidate 횟수")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("랜덤 후보 요청")
    void requestRandomCandidatesByWorldCupId() throws Exception {
        // given
        Integer teamCount = 4;
        Long worldCupId = 1L;

        PasswordDto passwordDto = new PasswordDto("1234");

        List<CandidateSimpleResponseDto> candidateSimpleResponseDtos = new ArrayList<>();
        for (long i = 1; i <= teamCount; i++) {
            candidateSimpleResponseDtos.add(new CandidateSimpleResponseDto(i, "테스트" + i, "테스트 이미지 url" + i, "테스트 썸네일 url" + i));
        }

        doNothing().when(worldCupService).verifyPassword(anyLong(), anyString());
        given(candidateService.findRandomCandidates(anyLong(), anyInt())).willReturn(candidateSimpleResponseDtos);

        String content = gson.toJson(passwordDto);

        // when
        ResultActions actions = mockMvc.perform(
                post("/worldcups/{worldCupId}/candidates/random", worldCupId)
                        .queryParam("teamCount", String.valueOf(teamCount))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Matchers.is(teamCount)))
                .andDo(document(
                        "requestRandomCandidatesByWorldCupId",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 패스 파라미터
                        pathParameters(parameterWithName("worldCupId").description("worldCup 식별자")
                                .attributes(key("constraints").value("1이상"))),
                        // 쿼리 파라미터
                        queryParameters(parameterWithName("teamCount").description("요청할 candidate 갯수").optional()
                                .attributes(
                                        key("default").value("4"),
                                        key("constraints").value("4, 8, 16, 32, ... 1024"))),
                        // 리퀘스트 바디
                        requestFields(fieldWithPath("password").type(JsonFieldType.STRING).description("worldCup 암호")
                                .attributes(key("constraints").value("미입력(null) 또는 4자리 숫자"))),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("[*].id").type(JsonFieldType.NUMBER).description("candidate 식별자"),
                                fieldWithPath("[*].name").type(JsonFieldType.STRING).description("candidate 이름"),
                                fieldWithPath("[*].image").type(JsonFieldType.STRING).description("이미지 주소"),
                                fieldWithPath("[*].thumb").type(JsonFieldType.STRING).description("썸네일 이미지 주소"))
                ))
        ;
    }

    @Test
    @DisplayName("후보 목록 가져오기")
    void requestCandidatesByWorldCupId() throws Exception {
        // given
        int page = 1;
        int size = 5;
        String sort = "winCount";
        String direction = "DESC";
        String keyword = null;
        long worldCupId = 1L;

        PasswordDto request = new PasswordDto("1234");

        List<CandidateResponseDto> data = new ArrayList<>();
        for (long i = 1; i <= size; i++) {
            CandidateResponseDto candidateResponseDto = CandidateResponseDto
                    .builder()
                    .id(i)
                    .name("테스트후보명" + i)
                    .image("테스트이미지url" + i)
                    .thumb("테스트썸네일url" + i)
                    .finalWinCount((int) (10L - i))
                    .winCount((int) (10L - i))
                    .matchUpWorldCupCount(100)
                    .matchUpGameCount(100)
                    .worldCupId(worldCupId)
                    .build();
            data.add(candidateResponseDto);
        }

        Page<CandidateResponseDto> responseDtos = new PageImpl(data, PageRequest.of(page - 1, size), 10L);

        doNothing().when(worldCupService).verifyPassword(anyLong(), anyString());
        given(candidateService.findCandidatesByWorldCupId(anyLong(), isNull(), any(Pageable.class))).willReturn(responseDtos);

        String content = gson.toJson(request);

        // when
        ResultActions actions = mockMvc.perform(
                post("/worldcups/{worldCupId}/candidates", worldCupId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", sort)
                        .param("direction", direction)
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "requestCandidatesByWorldCupId",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 패스 파라미터
                        pathParameters(parameterWithName("worldCupId").description("worldCup 식별자")
                                .attributes(key("constraints").value("1이상"))),
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
                                                        "winCount(제약조건 외 값 입력 시)"),
                                                key("constraints").value("winRatio(1대1 승률), finalWinRatio(최종 우승 비율), name(이름 순), matchUpWorldCupCount(월드컵 참가 횟수), matchUpGameCount(1대1 매칭 횟수), finalWinCount(최종 우승 횟수), createdAt(생성일시), winCount(1대1 승리 횟수)")
                                        ),
                                parameterWithName("direction").description("정렬 방법").optional()
                                        .attributes(
                                                key("default").value("DESC"),
                                                key("constraints").value("ASC 또는 DESC")
                                        ),
                                parameterWithName("keyword").description("검색어 (name에서 검색)").optional()
                                        .attributes(
//                                                key("default").value(""),
//                                                key("constraints").value("")
                                        )
                        ),
                        // 리퀘스트 바디
                        requestFields(fieldWithPath("password").type(JsonFieldType.STRING).description("worldCup 암호")
                                .attributes(key("constraints").value("미입력(null) 또는 4자리 숫자"))),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("pageInfo.first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지 여부"),
                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 당 게시물 수"),
                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("총 게시물 수"),
                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("pageInfo.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("data[*].id").type(JsonFieldType.NUMBER).description("candidate 식별자"),
                                fieldWithPath("data[*].name").type(JsonFieldType.STRING).description("candidate 이름"),
                                fieldWithPath("data[*].image").type(JsonFieldType.STRING).description("이미지 주소"),
                                fieldWithPath("data[*].thumb").type(JsonFieldType.STRING).description("썸네일 이미지 주소"),
                                fieldWithPath("data[*].finalWinCount").type(JsonFieldType.NUMBER).description("최종 우승 횟수"),
                                fieldWithPath("data[*].winCount").type(JsonFieldType.NUMBER).description("1대1 승리 횟수"),
                                fieldWithPath("data[*].matchUpWorldCupCount").type(JsonFieldType.NUMBER).description("월드컵 참가 횟수"),
                                fieldWithPath("data[*].matchUpGameCount").type(JsonFieldType.NUMBER).description("1대1 매칭 횟수"),
                                fieldWithPath("data[*].worldCupId").type(JsonFieldType.NUMBER).description("worldCup 식별자")
                        )
                ))
        ;

        String actual = actions.andReturn().getResponse().getContentAsString();
        String expected = gson.toJson(new PageResponseDto(responseDtos));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("특정 후보 삭제")
    void deleteCandidate() throws Exception {
        // given
        long candidateId = 1L;

        doNothing().when(candidateService).verifyAccess(anyString(), anyLong());
        doNothing().when(candidateService).deleteCandidate(anyLong());

        // when
        ResultActions actions = mockMvc.perform(
                delete("/candidates/{candidateId}", candidateId)
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
                        .with(csrf().asHeader())
        );

        // then
        actions.andExpect(status().isNoContent())
                .andDo(document("deleteCandidate",
                        preprocessRequest(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        // 패스 파라미터
                        pathParameters(
                                parameterWithName("candidateId").description("candidate 식별자")
                                        .attributes(key("constraints").value("1이상")))
                ))
        ;
    }
}