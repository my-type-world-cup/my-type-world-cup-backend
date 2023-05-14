package com.mytypeworldcup.mytypeworldcup.domain.like.controller;

import com.mytypeworldcup.mytypeworldcup.domain.comment.entity.Comment;
import com.mytypeworldcup.mytypeworldcup.domain.comment.service.CommentService;
import com.mytypeworldcup.mytypeworldcup.domain.like.service.LikeService;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser
class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberService memberService;

    @MockBean
    private CommentService commentService;
    @MockBean
    private LikeService likeService;

    @Test
    @DisplayName("좋아요")
    void postLike() throws Exception {
        // given
        Long commentId = 1L;

        given(memberService.findVerifiedMemberByEmail(anyString())).willReturn(new Member());
        given(commentService.findVerifiedCommentById(anyLong())).willReturn(new Comment());
        doNothing().when(likeService).createLike(any(Member.class), any(Comment.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/comments/{commentId}/likes", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        // then
        actions
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("좋아요 취소")
    void deleteLike() throws Exception {
        // given
        Long commentId = 1L;

        given(memberService.findVerifiedMemberByEmail(anyString())).willReturn(new Member());
        given(commentService.findVerifiedCommentById(anyLong())).willReturn(new Comment());
        doNothing().when(likeService).deleteLike(any(Member.class), any(Comment.class));

        // when
        ResultActions actions = mockMvc.perform(
                delete("/comments/{commentId}/likes", commentId)
                        .with(csrf())
        );

        // then
        actions
                .andExpect(status().isNoContent());
    }
}