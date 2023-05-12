package com.mytypeworldcup.mytypeworldcup.domain.candidate.controller;

import com.google.gson.Gson;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidatePatchDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateRequestDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.service.CandidateService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import com.mytypeworldcup.mytypeworldcup.global.common.PageResponseDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void patchMatchResults() throws Exception {
        // given
        List<CandidatePatchDto> patchDtos = new ArrayList<>();
        for (long i = 1; i <= 4; i++) {
            CandidatePatchDto candidatePatchDto = CandidatePatchDto
                    .builder()
                    .id(1L)
                    .matchUpGameCount(4)
                    .winCount(1)
                    .build();
            patchDtos.add(candidatePatchDto);
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

        CandidateRequestDto candidateRequestDto = CandidateRequestDto
                .builder()
                .worldCupId(worldCupId)
                .password(null)
                .build();

        List<CandidateSimpleResponseDto> candidateSimpleResponseDtos = new ArrayList<>();
        for (long i = 1; i <= teamCount; i++) {
            candidateSimpleResponseDtos.add(new CandidateSimpleResponseDto(i, "테스트" + i, "테스트url" + i));
        }

        doNothing().when(worldCupService).verifyPassword(anyLong(), anyString());
        given(candidateService.findRandomCandidates(anyLong(), anyInt())).willReturn(candidateSimpleResponseDtos);

        String content = gson.toJson(candidateRequestDto);

        // when
        ResultActions actions = mockMvc.perform(
                post("/candidates/random")
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

        CandidateRequestDto request = CandidateRequestDto
                .builder()
                .worldCupId(1L)
                .password(null)
                .build();

        List<CandidateResponseDto> data = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            CandidateResponseDto candidateResponseDto = CandidateResponseDto
                    .builder()
                    .id(i)
                    .name("테스트후보명" + i)
                    .image("테스트이미지url" + i)
                    .finalWinCount((int) (10L - i))
                    .winCount((int) (10L - i))
                    .matchUpWorldCupCount(100)
                    .matchUpGameCount(100)
                    .worldCupId(request.getWorldCupId())
                    .build();
            data.add(candidateResponseDto);
        }

        Page<CandidateResponseDto> responseDtos = new PageImpl(data);

        doNothing().when(worldCupService).verifyPassword(anyLong(), anyString());
        given(candidateService.findCandidatesByWorldCupId(anyLong(), isNull(), any(Pageable.class))).willReturn(responseDtos);

        String content = gson.toJson(request);

        // when
        ResultActions actions = mockMvc.perform(
                post("/candidates/search")
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
}