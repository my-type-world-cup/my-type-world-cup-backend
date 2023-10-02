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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "secure-a-server.dolpick.com", uriPort = 0)
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
    void postComment_anonymous() throws Exception {
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
                .likesCount(0)
                .isLiked(false)
                .build();

        given(commentService.createComment(any(CommentPostDto.class))).willReturn(response);

        String content = gson.toJson(requestBody);

        // when
        ResultActions actions = mockMvc.perform(
                post("/comments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
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
                .andExpect(jsonPath("$.worldCupId").value(response.getWorldCupId()))
                .andDo(document(
                        "postComment - anonymous",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 바디
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                                        .attributes(key("constraints").value("length : 1이상 255이하")),
                                fieldWithPath("candidateName").type(JsonFieldType.STRING).description("우승한 candidate 이름").optional()
                                        .attributes(key("constraints").value("length : 50이하")),
                                fieldWithPath("worldCupId").type(JsonFieldType.NUMBER).description("worldCup 식별자")
                                        .attributes(key("constraints").value("1이상"))
                        ),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("comment 식별자"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("candidateName").type(JsonFieldType.STRING).description("우승한 candidate 이름"),
                                fieldWithPath("likesCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 누름 여부(로그인 시)"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("댓글 작성일시"),
                                fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("댓글 수정일시"),
                                fieldWithPath("memberId").type(JsonFieldType.NULL).description("댓글 작성 member 식별자 (익명댓글일시 null)"),
                                fieldWithPath("nickname").type(JsonFieldType.NULL).description("댓글 작성 member 닉네임 (익명댓글일시 null)"),
                                fieldWithPath("worldCupId").type(JsonFieldType.NUMBER).description("worldCup 식별자"))
                ))
        ;
    }

    @Test
    @DisplayName("댓글 쓰기 - 로그인 했을 경우")
    void postComment_login() throws Exception {
        // given
        Long memberId = 1L;
        String nickname = "테스트유저";

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
                .nickname(nickname)
                .worldCupId(requestBody.getWorldCupId())
                .isLiked(false)
                .likesCount(0)
                .build();

        given(memberService.findMemberIdByEmail(anyString())).willReturn(memberId);
        given(commentService.createComment(any(CommentPostDto.class))).willReturn(response);

        String content = gson.toJson(requestBody);

        // when
        ResultActions actions = mockMvc.perform(
                post("/comments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
                        .with(csrf().asHeader())
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
                .andExpect(jsonPath("$.worldCupId").value(response.getWorldCupId()))
                .andDo(document(
                        "postComment - login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        // 리퀘스트 바디
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                                        .attributes(key("constraints").value("length : 1이상 255이하")),
                                fieldWithPath("candidateName").type(JsonFieldType.STRING).description("우승한 candidate 이름").optional()
                                        .attributes(key("constraints").value("length : 50이하")),
                                fieldWithPath("worldCupId").type(JsonFieldType.NUMBER).description("worldCup 식별자")
                                        .attributes(key("constraints").value("1이상"))
                        ),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("comment 식별자"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("candidateName").type(JsonFieldType.STRING).description("우승한 candidate 이름"),
                                fieldWithPath("likesCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 누름 여부"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("댓글 작성일시"),
                                fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("댓글 수정일시"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("댓글 작성 member 식별자 (익명댓글일시 null)"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("댓글 작성 member 닉네임 (익명댓글일시 null)"),
                                fieldWithPath("worldCupId").type(JsonFieldType.NUMBER).description("worldCup 식별자"))
                ))
        ;

        verify(memberService).findMemberIdByEmail(anyString());
        verify(worldCupService).findVerifiedWorldCup(anyLong());
    }

    @Test
    @DisplayName("댓글 조회")
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
                .isLiked(true)
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
                .isLiked(false)
                .likesCount(78)
                .createdAt(localDateTime)
                .modifiedAt(localDateTime)
                .memberId(3L)
                .nickname("윈터짱짱걸")
                .worldCupId(1L)
                .build();

        List<CommentResponseDto> comments = Arrays.asList(comment1, comment2);

        Page<CommentResponseDto> responseDtos = new PageImpl<>(comments);

        given(memberService.findMemberIdByEmail(anyString())).willReturn(1L);
        given(commentService.findCommentsByWorldCupId(anyLong(), anyLong(), any(Pageable.class))).willReturn(responseDtos);

        // when
        ResultActions actions = mockMvc.perform(
                get("/comments")
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
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
                .andExpect(jsonPath("$.data[0].isLiked").isBoolean())
                .andExpect(jsonPath("$.data[0].createdAt").value(comment1.getCreatedAt().toString()))
                .andExpect(jsonPath("$.data[0].modifiedAt").value(comment1.getModifiedAt().toString()))
                .andExpect(jsonPath("$.data[0].memberId").value(comment1.getMemberId()))
                .andExpect(jsonPath("$.data[0].nickname").value(comment1.getNickname()))
                .andExpect(jsonPath("$.data[0].worldCupId").value(comment1.getWorldCupId()))
                .andExpect(jsonPath("$.data[1].id").value(comment2.getId()))
                .andExpect(jsonPath("$.data[1].content").value(comment2.getContent()))
                .andExpect(jsonPath("$.data[1].candidateName").value(comment2.getCandidateName()))
                .andExpect(jsonPath("$.data[1].likesCount").value(comment2.getLikesCount()))
                .andExpect(jsonPath("$.data[1].isLiked").isBoolean())
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
                .andExpect(jsonPath("$.pageInfo.last").value(responseDtos.isLast()))
                .andDo(document(
                        "getCommentsByWorldCupId",
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰").optional()
                        ),
                        // 쿼리 파라미터
                        queryParameters(
                                parameterWithName("worldCupId").description("worldCup 식별자")
                                        .attributes(
                                                key("constraints").value("1이상")
                                        ),
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
                                                        "likesCount(제약조건 외 값 입력 시)"),
                                                key("constraints").value("createdAt(생성일시), likesCount(좋아요순)")
                                        ),
                                parameterWithName("direction").description("정렬 방법").optional()
                                        .attributes(
                                                key("default").value("DESC"),
                                                key("constraints").value("ASC 또는 DESC")
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
                                fieldWithPath("data[*].id").type(JsonFieldType.NUMBER).description("comment 식별자"),
                                fieldWithPath("data[*].content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("data[*].candidateName").type(JsonFieldType.STRING).description("우승한 candidate 이름"),
                                fieldWithPath("data[*].likesCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("data[*].isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 누름 여부(로그인 시)"),
                                fieldWithPath("data[*].createdAt").type(JsonFieldType.STRING).description("댓글 작성일시"),
                                fieldWithPath("data[*].modifiedAt").type(JsonFieldType.STRING).description("댓글 수정일시"),
                                fieldWithPath("data[*].memberId").type(JsonFieldType.NUMBER).description("댓글 작성 member 식별자 (익명댓글일시 null)"),
                                fieldWithPath("data[*].nickname").type(JsonFieldType.STRING).description("댓글 작성 member 닉네임 (익명댓글일시 null)"),
                                fieldWithPath("data[*].worldCupId").type(JsonFieldType.NUMBER).description("worldCup 식별자"))
                ))
        ;

        verify(commentService).findCommentsByWorldCupId(anyLong(), anyLong(), any(Pageable.class));
    }
}