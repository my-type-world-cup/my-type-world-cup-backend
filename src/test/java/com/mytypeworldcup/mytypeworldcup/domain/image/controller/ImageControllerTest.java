package com.mytypeworldcup.mytypeworldcup.domain.image.controller;

import com.mytypeworldcup.mytypeworldcup.infrastructure.image.search.ImageSearchAPIAdapter;
import com.mytypeworldcup.mytypeworldcup.infrastructure.image.upload.ImageUploadAPIAdapter;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "secure-a-server.dolpick.com", uriPort = 0)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser
class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ImageSearchAPIAdapter imageSearchAPI;
    @MockBean
    private ImageUploadAPIAdapter imageUploadAPI;

    @Test
    @DisplayName("이미지 검색")
    void getImages() throws Exception {
        // given
        int page = 1;
        int size = 5;
        String keyword = "테스트";

        long total = 100L;
        Page<String> response = new PageImpl<>(
                List.of("test-image-url1",
                        "test-image-url2",
                        "test-image-url3",
                        "test-image-url4",
                        "test-image-url5"),
                PageRequest.of(page - 1, size),
                total
        );

        given(imageSearchAPI.searchImages(anyString(), any(Pageable.class))).willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
                get("/images")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("keyword", keyword)
                        .header(HttpHeaders.AUTHORIZATION, "Encoded Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "getImages",
                        preprocessResponse(prettyPrint()),
                        // 리퀘스트 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 인증 토큰")
                        ),
                        // 쿼리 파라미터
                        queryParameters(
                                parameterWithName("page").description("요청 페이지").optional()
                                        .attributes(
                                                key("default").value("1"),
                                                key("constraints").value("1이상")
                                        ),
                                parameterWithName("size").description("페이지 당 데이터 수").optional()
                                        .attributes(
                                                key("default").value("10"),
                                                key("constraints").value("1이상")
                                        ),
                                parameterWithName("keyword").description("검색어")
                        ),
                        // 리스폰스 바디
                        responseFields(
                                fieldWithPath("pageInfo.first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지 여부"),
                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 당 게시물 수"),
                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("총 게시물 수"),
                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("pageInfo.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("data[*]").type(JsonFieldType.ARRAY).description("이미지 url 리스트")
                        )
                ))
        ;
    }
}