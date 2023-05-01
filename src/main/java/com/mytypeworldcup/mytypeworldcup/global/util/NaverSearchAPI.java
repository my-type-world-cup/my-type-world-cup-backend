package com.mytypeworldcup.mytypeworldcup.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytypeworldcup.mytypeworldcup.global.common.NaverItemResponseDto;
import com.mytypeworldcup.mytypeworldcup.global.common.NaverResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NaverSearchAPI {
    @Value("${naver.search.api.id}")
    private String clientId;
    @Value("${naver.search.api.secret}")
    private String clientSecret;

    private final ObjectMapper objectMapper;

    public Page<String> getImageUrls(String keyword, Pageable pageable) {
        int display = pageable.getPageSize();
        int start = pageable.getPageNumber() * display + 1; // 네이버 api 형식에 맞게 변환

        // 네이버 검색 API 요청
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/image")
                .queryParam("query", keyword)
                .queryParam("display", display)
                .queryParam("start", start)
//                .queryParam("sort", "sim") // sim(기본값) 정확도순정렬, date 날짜순 내림차순
//                .queryParam("filter","all") // all(기본값), large, medium, small -> 이미지 크기
                .encode()
                .build()
                .toUri();

        // Spring 요청 제공 클래스 및 헤더 시크릿 키 설정
        RequestEntity<Void> request = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", this.clientId)
                .header("X-Naver-Client-Secret", this.clientSecret)
                .build();

        // Spring 제공 restTemplate
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        // JSON 파싱 (Json 문자열 -> NaverResponseDto 변환)
        NaverResponseDto naverResponseDto;
        try {
            naverResponseDto = objectMapper.readValue(response.getBody(), NaverResponseDto.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e); // TODO 에러 관련 처리
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO 에러 관련 처리
        }

        // 객체에서 이미지링크만 파싱
        List<String> images = new ArrayList<>();
        for (NaverItemResponseDto item : naverResponseDto.getItems()) {
            images.add(item.getLink());
        }

        return new PageImpl<>(images, pageable, naverResponseDto.getTotal());
    }

}