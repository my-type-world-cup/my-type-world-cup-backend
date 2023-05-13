package com.mytypeworldcup.mytypeworldcup.infrastructure.image;

import com.mytypeworldcup.mytypeworldcup.infrastructure.image.dto.NaverItemResponseDto;
import com.mytypeworldcup.mytypeworldcup.infrastructure.image.dto.NaverResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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
    private final WebClient webClient;

    public Page<String> searchImages(String keyword, Pageable pageable) {
        int display = pageable.getPageSize();
        int start = pageable.getPageNumber() * display + 1; // 네이버 api 형식에 맞게 변환

        // 네이버 검색 API 요청
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com/v1/search/image")
                .queryParam("query", keyword)
                .queryParam("display", display)
                .queryParam("start", start)
//                .queryParam("sort", "sim") // sim(기본값) 정확도순정렬, date 날짜순 내림차순
//                .queryParam("filter","all") // all(기본값), large, medium, small -> 이미지 크기
                .encode()
                .build()
                .toUri();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", this.clientId);
        headers.set("X-Naver-Client-Secret", this.clientSecret);

        // WebClient를 사용하여 API 호출
        NaverResponseDto naverResponseDto = webClient
                .get()
                .uri(uri)
                .headers(h -> h.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(NaverResponseDto.class)
                .block();

        // 객체에서 이미지링크만 파싱
        List<String> images = new ArrayList<>();
        for (NaverItemResponseDto item : naverResponseDto.getItems()) {
            images.add(item.getLink());
        }

        return new PageImpl<>(images, pageable, naverResponseDto.getTotal());
    }
}