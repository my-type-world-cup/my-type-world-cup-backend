package com.mytypeworldcup.mytypeworldcup.infrastructure.image.upload.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageResponseDto {
    private String image;
    private String thumb;

    @Builder
    public ImageResponseDto(String image,
                            String thumb) {
        this.image = image;
        this.thumb = thumb;
    }
}
