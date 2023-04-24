package com.mytypeworldcup.mytypeworldcup.global.util;

import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import com.mytypeworldcup.mytypeworldcup.global.error.CommonExceptionCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImageCrawler {
    public List<String> getImageUrls(String searchKeyword) {
        Document document;

        try {
            document = Jsoup.connect("https://www.google.com/search?q=" + searchKeyword + "&tbm=isch").get();
        } catch (IOException e) {
            throw new BusinessLogicException(CommonExceptionCode.SERVICE_UNAVAILABLE);
        }

        Elements imageElements = document.getElementsByTag("img");

        List<String> imageUrls = new ArrayList<>();
        for (int i = 1; i < imageElements.size(); i++) {
            imageUrls.add(imageElements.get(i).attr("src"));
        }

        return imageUrls;
    }
}
