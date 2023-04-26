package com.mytypeworldcup.mytypeworldcup.domain.candidate.controller;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidatePatchDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.service.CandidateService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.dto.PasswordDto;
import com.mytypeworldcup.mytypeworldcup.global.util.ImageCrawler;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
public class CandidateController {
    private final ImageCrawler imageCrawler;
    private final WorldCupService worldCupService;
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

        Map<String, Integer> result = new HashMap<>();
        result.put("updatedCandidates", candidatePatchDtos.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 본격적인 월드컵 시작을 위해 월드컵에 사용될 Candidate들을 요청<p>
     * 비밀번호를 입력받아야 하므로 POST 를 사용하였음
     */
    @PostMapping("/worldcups/{worldCupId}/candidates/random")
    public ResponseEntity requestRandomCandidatesByWorldCupId(@Positive @PathVariable Long worldCupId,
                                                              @RequestParam int teamCount,
                                                              @RequestBody PasswordDto passwordDto) {
        worldCupService.verifyPassword(worldCupId, passwordDto.getPassword());
        List<CandidateSimpleResponseDto> responseDtos = candidateService.findRandomCandidates(worldCupId, teamCount);

        return ResponseEntity.ok(responseDtos);
    }

    /**
     * 특정 WorldCup 에 속한 모든 Candidate 들을 요청<P>
     * 비밀번호를 입력받아야 하므로 POST 를 사용하였음
     */
    @PostMapping("/worldcups/{worldCupId}/candidates")
    public ResponseEntity requestCandidatesByWorldCupId(@Positive @PathVariable Long worldCupId,
                                                        @RequestBody PasswordDto passwordDto) {
        worldCupService.verifyPassword(worldCupId, passwordDto.getPassword());
        List<CandidateResponseDto> responseDtos = candidateService.findCandidatesByWorldCupId(worldCupId);

        return ResponseEntity.ok(responseDtos);
    }

}
