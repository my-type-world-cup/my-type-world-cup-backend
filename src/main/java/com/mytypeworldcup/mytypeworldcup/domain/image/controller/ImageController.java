package com.mytypeworldcup.mytypeworldcup.domain.image.controller;

import com.mytypeworldcup.mytypeworldcup.global.common.PageResponseDto;
import com.mytypeworldcup.mytypeworldcup.infrastructure.image.search.ImageSearchAPIAdapter;
import com.mytypeworldcup.mytypeworldcup.infrastructure.image.upload.ImageUploadAPIAdapter;
import com.mytypeworldcup.mytypeworldcup.infrastructure.image.upload.dto.ImageResponseDto;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageSearchAPIAdapter imageSearchAPI;
    private final ImageUploadAPIAdapter imageUploadAPI;

    @GetMapping("/images")
    public ResponseEntity getImages(@Positive @RequestParam(required = false, defaultValue = "1") int page,
                                    @Positive @RequestParam(required = false, defaultValue = "10") int size,
                                    @RequestParam String keyword) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<String> imageUrls = imageSearchAPI.searchImages(keyword, pageRequest);

        return ResponseEntity.ok(new PageResponseDto(imageUrls));
    }

    @PostMapping("/images")
    public ResponseEntity postImage(@RequestParam MultipartFile file) {
        ImageResponseDto imageResponseDto = imageUploadAPI.uploadImage(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageResponseDto);
    }
}
