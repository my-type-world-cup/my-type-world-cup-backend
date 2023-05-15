package com.mytypeworldcup.mytypeworldcup.infrastructure.image.upload.imgbb;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImgBBImage {
    private String filename;
    private String name;
    private String mime;
    private String extension;
    private String url;
}
