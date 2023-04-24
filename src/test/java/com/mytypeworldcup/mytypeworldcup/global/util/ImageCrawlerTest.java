package com.mytypeworldcup.mytypeworldcup.global.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ImageCrawlerTest {
    @Autowired
    private ImageCrawler imageCrawler;

    @Test
    void getImageUrls() {
        // given
        String searchKeyword = "이상형월드컵";

        // when
        List<String> imageUrls = imageCrawler.getImageUrls(searchKeyword);

        // then
        Assertions.assertEquals(20, imageUrls.size());

    }
}