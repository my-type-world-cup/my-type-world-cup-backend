package com.mytypeworldcup.mytypeworldcup.infrastructure.image.upload;

import com.mytypeworldcup.mytypeworldcup.infrastructure.image.upload.dto.ImageResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadAPIAdapter {
    ImageResponseDto uploadImage(MultipartFile file);
}
