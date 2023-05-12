package com.mytypeworldcup.mytypeworldcup.domain.image.controller;

import com.mytypeworldcup.mytypeworldcup.domain.image.api.NaverSearchAPI;
import com.mytypeworldcup.mytypeworldcup.global.common.PageResponseDto;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final NaverSearchAPI naverSearchAPI;

    @GetMapping("/images")
    public ResponseEntity getImages(@Positive @RequestParam(required = false, defaultValue = "1") int page,
                                    @Positive @RequestParam(required = false, defaultValue = "10") int size,
                                    @RequestParam String keyword) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<String> imageUrls = naverSearchAPI.searchImages(keyword, pageRequest);

        return ResponseEntity.ok(new PageResponseDto(imageUrls));
    }
}
