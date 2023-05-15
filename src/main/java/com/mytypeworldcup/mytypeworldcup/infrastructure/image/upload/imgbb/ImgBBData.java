package com.mytypeworldcup.mytypeworldcup.infrastructure.image.upload.imgbb;

import lombok.Getter;

@Getter
public class ImgBBData {
    private String id;
    private String title;
    private String urlViewer;
    private String url;
    private String displayUrl;
    private int width;
    private int height;
    private int size;
    private long time;
    private int expiration;
    private ImgBBImage image;
    private ImgBBImage thumb;
    private ImgBBImage medium;
    private String deleteUrl;
}
