package com.mytypeworldcup.mytypeworldcup.domain.like.controller;

import com.google.gson.Gson;
import com.mytypeworldcup.mytypeworldcup.domain.like.dto.LikePostDto;
import com.mytypeworldcup.mytypeworldcup.domain.like.service.LikeService;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser
class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @MockBean
    private MemberService memberService;

    @MockBean
    private LikeService likeService;

    @Test
    @DisplayName("좋아요")
    void postLike() throws Exception {
        // given
        Long commentId = 1L;
        Long memberId = 4L;
        Long likeId = 10L;

        LikePostDto request = LikePostDto
                .builder()
                .commentId(commentId)
                .build();

        given(memberService.findMemberIdByEmail(anyString())).willReturn(memberId);
        given(likeService.createLike(any(LikePostDto.class))).willReturn(likeId);

        String content = gson.toJson(request);

        // when
        ResultActions actions = mockMvc.perform(
                post("/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/likes/" + likeId));
    }

    @Test
    @DisplayName("좋아요 취소")
    void deleteLike() throws Exception {
        // given
        Long likeId = 10L;
        Long memberId = 4L;

        given(memberService.findMemberIdByEmail(anyString())).willReturn(memberId);
        doNothing().when(likeService).deleteLike(anyLong(), anyLong());

        // when
        ResultActions actions = mockMvc.perform(
                delete("/likes/{likeId}", likeId)
                        .with(csrf())
        );

        // then
        actions
                .andExpect(status().isNoContent());
    }
}