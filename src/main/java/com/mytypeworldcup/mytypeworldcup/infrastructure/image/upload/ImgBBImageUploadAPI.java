package com.mytypeworldcup.mytypeworldcup.infrastructure.image.upload;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Component
@RequiredArgsConstructor
public class ImgBBImageUploadAPI implements ImageUploadAPIAdapter {

    private final WebClient webClient;
    @Value("${imgbb.secret}")
    private String secret;

    @Override
    @SneakyThrows
    public void uploadImage(MultipartFile file) {
        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 요청 바디 설정
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", secret);
        body.add("image", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        // API 엔드포인트
        String apiUrl = "https://api.imgbb.com/1/upload";

        // POST 요청 보내기
        String response = webClient.post()
                .uri(apiUrl)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 응답 확인
        System.out.println(response);
    }

}