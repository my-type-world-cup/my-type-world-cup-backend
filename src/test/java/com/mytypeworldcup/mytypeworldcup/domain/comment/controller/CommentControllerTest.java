package com.mytypeworldcup.mytypeworldcup.domain.comment.controller;

import com.google.gson.Gson;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.service.CommentService;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser
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
                .candidateName("카리나")
                .worldCupId(5L)
                .build();

        CommentResponseDto response = CommentResponseDto
                .builder()
                .id(1L)
                .content(requestBody.getContent())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .candidateName(requestBody.getCandidateName())
                .nickname(null)
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
                .andExpect(jsonPath("$.candidateName").value(response.getCandidateName()))
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
    void postComment_Member() throws Exception {
        // given
        Long memberId = 1L;

        CommentPostDto requestBody = CommentPostDto
                .builder()
                .content("로그인한 사용자가 작성한 댓글입니다")
                .candidateName("카리나")
                .worldCupId(5L)
                .build();

        CommentResponseDto response = CommentResponseDto
                .builder()
                .id(1L)
                .content(requestBody.getContent())
                .candidateName(requestBody.getCandidateName())
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
                .andExpect(jsonPath("$.candidateName").value(response.getCandidateName()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.modifiedAt").exists())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.nickname").value(response.getNickname()))
                .andExpect(jsonPath("$.worldCupId").value(response.getWorldCupId()));

        verify(memberService).findMemberIdByEmail(anyString());
        verify(worldCupService).findWorldCup(anyLong());
    }

    @Test
    @DisplayName("특정 월드컵에 속한 댓글 가져오기")
    void getCommentsByWorldCupId() throws Exception {
        // given
        Long worldCupId = 1L;
        int page = 1;
        int size = 5;
        String sort = "likesCount";
        Sort.Direction direction = Sort.Direction.DESC;
        LocalDateTime localDateTime = LocalDateTime.of(2023, 05, 10, 00, 51, 15);

        CommentResponseDto comment1 = CommentResponseDto
                .builder()
                .id(1L)
                .content("카리나가 짱이지!")
                .candidateName("카리나")
                .likesCount(100)
                .createdAt(localDateTime)
                .modifiedAt(localDateTime)
                .memberId(5L)
                .nickname("카리나가좋아요")
                .worldCupId(1L)
                .build();
        CommentResponseDto comment2 = CommentResponseDto
                .builder()
                .id(2L)
                .content("윈터가 짱이지!")
                .candidateName("윈터")
                .likesCount(78)
                .createdAt(localDateTime)
                .modifiedAt(localDateTime)
                .memberId(3L)
                .nickname("윈터짱짱걸")
                .worldCupId(1L)
                .build();

        List<CommentResponseDto> comments = Arrays.asList(comment1, comment2);

        Page<CommentResponseDto> responseDtos = new PageImpl<>(comments);

        given(commentService.findCommentsByWorldCupId(anyLong(), any(Pageable.class))).willReturn(responseDtos);

        // when
        ResultActions actions = mockMvc.perform(
                get("/comments")
                        .param("worldCupId", String.valueOf(worldCupId))
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", sort)
                        .param("direction", direction.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(comment1.getId()))
                .andExpect(jsonPath("$.data[0].content").value(comment1.getContent()))
                .andExpect(jsonPath("$.data[0].candidateName").value(comment1.getCandidateName()))
                .andExpect(jsonPath("$.data[0].likesCount").value(comment1.getLikesCount()))
                .andExpect(jsonPath("$.data[0].createdAt").value(comment1.getCreatedAt().toString()))
                .andExpect(jsonPath("$.data[0].modifiedAt").value(comment1.getModifiedAt().toString()))
                .andExpect(jsonPath("$.data[0].memberId").value(comment1.getMemberId()))
                .andExpect(jsonPath("$.data[0].nickname").value(comment1.getNickname()))
                .andExpect(jsonPath("$.data[0].worldCupId").value(comment1.getWorldCupId()))
                .andExpect(jsonPath("$.data[1].id").value(comment2.getId()))
                .andExpect(jsonPath("$.data[1].content").value(comment2.getContent()))
                .andExpect(jsonPath("$.data[1].candidateName").value(comment2.getCandidateName()))
                .andExpect(jsonPath("$.data[1].likesCount").value(comment2.getLikesCount()))
                .andExpect(jsonPath("$.data[1].createdAt").value(comment2.getCreatedAt().toString()))
                .andExpect(jsonPath("$.data[1].modifiedAt").value(comment2.getModifiedAt().toString()))
                .andExpect(jsonPath("$.data[1].memberId").value(comment2.getMemberId()))
                .andExpect(jsonPath("$.data[1].nickname").value(comment2.getNickname()))
                .andExpect(jsonPath("$.data[1].worldCupId").value(comment2.getWorldCupId()))
                .andExpect(jsonPath("$.pageInfo.first").value(responseDtos.isFirst()))
                .andExpect(jsonPath("$.pageInfo.page").value(responseDtos.getNumber() + 1))
                .andExpect(jsonPath("$.pageInfo.size").value(responseDtos.getSize()))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(responseDtos.getTotalElements()))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(responseDtos.getTotalPages()))
                .andExpect(jsonPath("$.pageInfo.last").value(responseDtos.isLast()));

        verify(commentService).findCommentsByWorldCupId(anyLong(), any(Pageable.class));
    }
}