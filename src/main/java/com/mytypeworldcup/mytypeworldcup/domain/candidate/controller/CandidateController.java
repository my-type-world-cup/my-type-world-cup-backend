package com.mytypeworldcup.mytypeworldcup.domain.candidate.controller;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.service.CandidateService;
import com.mytypeworldcup.mytypeworldcup.global.util.ImageCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CandidateController {
    private final ImageCrawler imageCrawler;
    private final CandidateService candidateService;

    @GetMapping("/candidates/images/search")
    public ResponseEntity getImagesByKeyword(@RequestParam String keyword) {
        List<String> imageUrls = imageCrawler.getImageUrls(keyword);
        return ResponseEntity.ok(imageUrls);
    }
}
