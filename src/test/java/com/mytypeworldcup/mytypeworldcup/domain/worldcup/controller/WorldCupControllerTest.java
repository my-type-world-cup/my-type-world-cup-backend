package com.mytypeworldcup.mytypeworldcup.domain.worldcup.controller;

import com.google.gson.Gson;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupInfoResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import com.mytypeworldcup.mytypeworldcup.global.common.PageResponseDto;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorldCupController.class)
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
                .password(null) // 공개 = null
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
                        .with(csrf())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(worldCupId))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.password").isEmpty())
                .andExpect(jsonPath("$.memberId").value(memberId));
    }

    @DisplayName("월드컵 가져오기 - 메인페이지")
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
                .andExpect(status().isOk());

        String actual = actions.andReturn().getResponse().getContentAsString();
        String expected = gson.toJson(new PageResponseDto(responseDtos));

        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("특정 월드컵 요청")
    @Test
    void getWorldCup() throws Exception {
        // given
        long worldCupId = 1L;
        WorldCupInfoResponseDto response = WorldCupInfoResponseDto
                .builder()
                .id(worldCupId)
                .title("테스트 타이틀")
                .description("테스트 디스크립션")
                .visibility(true)
                .candidatesCount(10)
                .build();

        given(worldCupService.findWorldCup(anyLong())).willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
                get("/worldcups/" + worldCupId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk());

        String expected = gson.toJson(response);
        String actual = actions.andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(expected, actual);

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
        );

        // then
        actions.andExpect(status().isOk());
        verify(memberService).findMemberIdByEmail(anyString());
        verify(worldCupService).searchWorldCups(anyLong(), anyString(), any(Pageable.class));

        String actual = actions.andReturn().getResponse().getContentAsString();
        String expected = gson.toJson(new PageResponseDto(responseDtos));

        Assertions.assertEquals(expected, actual);
    }
}
