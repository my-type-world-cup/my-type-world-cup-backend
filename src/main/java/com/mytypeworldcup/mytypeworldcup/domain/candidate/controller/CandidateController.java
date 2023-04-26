package com.mytypeworldcup.mytypeworldcup.domain.candidate.controller;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidatePatchDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.service.CandidateService;
import com.mytypeworldcup.mytypeworldcup.global.util.ImageCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 경기 결과를 반영하는 메서드<p>
     * 결과를 리스트로 받는다
     */
    @PatchMapping("/candidates")
    public ResponseEntity patchMatchResults(@RequestBody List<CandidatePatchDto> candidatePatchDtos) {
        candidateService.updateMatchResults(candidatePatchDtos);
        return ResponseEntity.ok("업데이트가 정상적으로 처리되었습니다.");
    }

}
