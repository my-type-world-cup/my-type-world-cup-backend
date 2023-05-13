package com.mytypeworldcup.mytypeworldcup.infrastructure.image.upload;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadAPIAdapter {
    void uploadImage(MultipartFile file);
}
