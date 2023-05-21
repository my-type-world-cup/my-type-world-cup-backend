package com.mytypeworldcup.mytypeworldcup.domain.candidate.controller;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.MatchDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidatePostDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.service.CandidateService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import com.mytypeworldcup.mytypeworldcup.global.common.PageResponseDto;
import com.mytypeworldcup.mytypeworldcup.global.common.PasswordDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
public class CandidateController {
    private final WorldCupService worldCupService;
    private final CandidateService candidateService;

    @PostMapping("/candidates")
    public ResponseEntity postCandidate(Authentication authentication,
                                        @RequestBody CandidatePostDto candidatePostDto) {
        worldCupService.verifyWorldCupAccess(authentication.getName(), candidatePostDto.getWorldCupId());
        CandidateResponseDto candidateResponseDto = candidateService.createCandidate(candidatePostDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateResponseDto);
    }

    /**
     * 경기 결과를 반영하는 메서드<p>
     * 결과를 리스트로 받는다
     */
    @PatchMapping("/candidates")
    public ResponseEntity patchMatchResults(@RequestBody List<MatchDto> matchDtos) {
        candidateService.updateMatchResults(matchDtos);

        Map<String, Integer> result = new HashMap<>();
        result.put("updatedCandidates", matchDtos.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 본격적인 월드컵 시작을 위해 월드컵에 사용될 Candidate들을 요청<p>
     * 비밀번호를 입력받아야 하므로 POST 를 사용하였음
     */
    @PostMapping("/worldcups/{worldCupId}/candidates/random")
    public ResponseEntity requestRandomCandidatesByWorldCupId(@Positive @PathVariable long worldCupId,
                                                              @RequestParam(required = false, defaultValue = "4") Integer teamCount,
                                                              @Valid @RequestBody PasswordDto passwordDto) {
        worldCupService.verifyPassword(worldCupId, passwordDto.getPassword());
        List<CandidateSimpleResponseDto> responseDtos = candidateService.findRandomCandidates(worldCupId, teamCount);

        return ResponseEntity.ok(responseDtos);
    }

    /**
     * 특정 WorldCup 에 속한 모든 Candidate 들을 요청(랭킹,수정페이지에서 사용예정)<P>
     * 비밀번호를 입력받아야 하므로 POST 를 사용하였음<p>
     * 파라미터 설명<p>
     * page = 요청할 페이지(default=1) !!자체적으로 -1해서 계산함!!<p>
     * size = 페이지당 볼 게시물 수(default=5)<p>
     * sort = winCount(1대1 이긴횟수 default), finalWinCount(최종 우승 횟수), matchUpGameCount(1대1 매칭 횟수),
     * matchUpWorldCupCount(월드컵 참가 횟수), name(이름순), finalWinRatio(최종우승비율), winRatio(1대1 승률)<p>
     * direction = DESC(내림차순, default), ASC(오름차순)<p>
     * keyword = 검색어 (name 에서 검색)
     */
    @PostMapping("/worldcups/{worldCupId}/candidates")
    public ResponseEntity requestCandidatesByWorldCupId(@Positive @PathVariable long worldCupId,
                                                        @Positive @RequestParam(required = false, defaultValue = "1") int page,
                                                        @Positive @RequestParam(required = false, defaultValue = "5") int size,
                                                        @RequestParam(required = false, defaultValue = "winCount") String sort,
                                                        @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                                        @RequestParam(required = false) String keyword,
                                                        @Valid @RequestBody PasswordDto passwordDto) {
        worldCupService.verifyPassword(worldCupId, passwordDto.getPassword());

        PageRequest pageRequest = PageRequest.of(page - 1, size, direction, sort);
        Page<CandidateResponseDto> responseDtos = candidateService.findCandidatesByWorldCupId(worldCupId, keyword, pageRequest);

        return ResponseEntity.ok(new PageResponseDto(responseDtos));
    }

}
