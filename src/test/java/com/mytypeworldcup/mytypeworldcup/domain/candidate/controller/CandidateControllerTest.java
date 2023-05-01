package com.mytypeworldcup.mytypeworldcup.domain.candidate.controller;

import com.google.gson.Gson;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidatePatchDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.service.CandidateService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import com.mytypeworldcup.mytypeworldcup.global.util.NaverSearchAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private NaverSearchAPI naverSearchAPI;
    @MockBean
    private WorldCupService worldCupService;
    @MockBean
    private CandidateService candidateService;

    @Test
    void getImagesByKeyword() {
    }

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
    void requestRandomCandidatesByWorldCupId() {
    }

    @Test
    void requestCandidatesByWorldCupId() {
    }
}