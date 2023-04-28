package com.mytypeworldcup.mytypeworldcup.domain.comment.controller;

import com.google.gson.Gson;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.service.CommentService;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.GetWorldCupResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @MockBean
    private CommentService commentService;
    @MockBean
    private MemberService memberService;
    @MockBean
    private WorldCupService worldCupService;

    @Test
    @DisplayName("댓글 쓰기 - 비회원일 경우")
    void postComment_anonymousMember(WebApplicationContext webApplicationContext) throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        // given
        CommentPostDto requestBody = CommentPostDto
                .builder()
                .content("익명의 사용자가 댓글을 작성했습니다.")
                .worldCupId(5L)
                .build();

        CommentResponseDto response = CommentResponseDto
                .builder()
                .id(1L)
                .content(requestBody.getContent())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .nickname("익명")
                .memberId(null)
                .worldCupId(requestBody.getWorldCupId())
                .build();

        given(commentService.createComment(any(CommentPostDto.class))).willReturn(response);

        String content = gson.toJson(requestBody);

        // when
        ResultActions actions = mockMvc.perform(
                post("/comments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.content").value(response.getContent()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.modifiedAt").exists())
                .andExpect(jsonPath("$.memberId").isEmpty())
                .andExpect(jsonPath("$.nickname").value(response.getNickname()))
                .andExpect(jsonPath("$.worldCupId").value(response.getWorldCupId()));

        verifyNoInteractions(memberService);
        verify(worldCupService).findWorldCup(anyLong());
    }

    @Test
    @DisplayName("댓글 쓰기 - 로그인 했을 경우")
    @WithMockUser // 로그인 한 상황 가정
    void postComment_Member() throws Exception {
        // given
        Long memberId = 1L;

        CommentPostDto requestBody = CommentPostDto
                .builder()
                .content("로그인한 사용자가 작성한 댓글입니다")
                .worldCupId(5L)
                .build();

        CommentResponseDto response = CommentResponseDto
                .builder()
                .id(1L)
                .content(requestBody.getContent())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .memberId(memberId)
                .worldCupId(requestBody.getWorldCupId())
                .build();

        given(memberService.findMemberIdByEmail(anyString())).willReturn(memberId);
        given(commentService.createComment(any(CommentPostDto.class))).willReturn(response);

        String content = gson.toJson(requestBody);

        // when
        ResultActions actions = mockMvc.perform(
                post("/comments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.content").value(response.getContent()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.modifiedAt").exists())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.nickname").value(response.getNickname()))
                .andExpect(jsonPath("$.worldCupId").value(response.getWorldCupId()));

        verify(memberService).findMemberIdByEmail(anyString());
        verify(worldCupService).findWorldCup(anyLong());
    }
}