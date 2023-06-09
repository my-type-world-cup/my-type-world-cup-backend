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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidateController.class)
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
                        .with(csrf())
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
                .andExpect(jsonPath("$.worldCupId").value(response.getWorldCupId()));
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
                        .with(csrf())
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
                .andExpect(jsonPath("$.worldCupId").value(response.getWorldCupId()));
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
                        .with(csrf())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedCandidates").value(patchDtos.size()));
    }

    @Test
    @DisplayName("랜덤이미지요청")
    void requestRandomCandidatesByWorldCupId() throws Exception {
        // given
        Integer teamCount = 16;
        Long worldCupId = 1L;

        PasswordDto passwordDto = new PasswordDto(null);

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
                        .with(csrf())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", Matchers.is(teamCount)));
    }

    @Test
    @DisplayName("후보목록 가져오기")
    void requestCandidatesByWorldCupId() throws Exception {
        // given
        int page = 1;
        int size = 5;
        String sort = "winCount";
        String direction = "DESC";
        String keyword = null;
        long worldCupId = 1L;

        PasswordDto request = new PasswordDto(null);

        List<CandidateResponseDto> data = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
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

        Page<CandidateResponseDto> responseDtos = new PageImpl(data);

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
                        .with(csrf())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isOk());

        String actual = actions.andReturn().getResponse().getContentAsString();
        String expected = gson.toJson(new PageResponseDto(responseDtos));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void deleteCandidate() throws Exception {
        // given
        long candidateId = 1L;

        doNothing().when(candidateService).verifyAccess(anyString(), anyLong());
        doNothing().when(candidateService).deleteCandidate(anyLong());

        // when
        ResultActions actions = mockMvc.perform(
                delete("/candidates/{candidateId}", candidateId)
                        .with(csrf())
        );

        // then
        actions.andExpect(status().isNoContent());
    }
}