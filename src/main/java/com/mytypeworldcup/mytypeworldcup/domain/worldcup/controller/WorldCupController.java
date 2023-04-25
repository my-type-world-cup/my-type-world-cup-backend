package com.mytypeworldcup.mytypeworldcup.domain.worldcup.controller;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.service.CandidateService;
import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupRequestDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import com.mytypeworldcup.mytypeworldcup.global.common.PageResponseDto;
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

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class WorldCupController {
    private final WorldCupService worldCupService;
    private final CandidateService candidateService;
    private final MemberService memberService;

    @PostMapping("/worldcups")
    public ResponseEntity postWorldCup(Authentication authentication,
                                       @RequestBody @Valid WorldCupPostDto worldCupPostDto) {
        // 멤버아이디 설정 -> jwt토큰의 이메일을 기반으로 멤버아이디를 검색
        Long memberId = memberService.findMemberIdByEmail(authentication.getName());
        worldCupPostDto.setMemberId(memberId);

        // WorldCup 생성
        WorldCupResponseDto worldCupResponseDto = worldCupService.createWorldCup(worldCupPostDto);

        // 생성된 WorldCup의 Id값을 세팅 후 Candidate 생성
        worldCupPostDto.setWorldCupIdForCandidatePostDtos(worldCupResponseDto.getId());
        List<CandidateResponseDto> candidateResponseDtos = candidateService.createCandidates(worldCupPostDto.getCandidatePostDtos());

        // 생성된 값들을 세팅 후 리턴
        worldCupResponseDto.setCandidateResponseDtos(candidateResponseDtos);

        return ResponseEntity.status(HttpStatus.CREATED).body(worldCupResponseDto);
    }

    /**
     * 파라미터 설명<p>
     * page = 원하는 페이지 !!자체적으로 -1해서 계산함!!<p>
     * size = 페이지당 볼 게시물 수<p>
     * sort = playCount(인기순), createdAt(생성일순), commentCount(댓글순)<p>
     * direction = DESC(내림차순), ASC(오름차순)<p>
     * keyword = 검색어 (title, description 에서 검색)
     */
    @GetMapping("/worldcups")
    public ResponseEntity getWorldCups(@Positive @RequestParam(required = false, defaultValue = "1") int page,
                                       @Positive @RequestParam(required = false, defaultValue = "5") int size,
                                       @RequestParam(required = false, defaultValue = "playCount") String sort,
                                       @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                       @RequestParam(required = false) String keyword) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, direction, sort);
        Page<WorldCupSimpleResponseDto> responseDtos = worldCupService.searchWorldCups(pageRequest, keyword);

        return ResponseEntity.ok(new PageResponseDto(responseDtos));
    }

    @GetMapping("/worldcups/{worldcup-id}")
    public ResponseEntity getWorldCup(@Positive @PathVariable("worldcup-id") long worldCupId) {
        return ResponseEntity.ok(worldCupService.findWorldCup(worldCupId));
    }

    /**
     * 본격적인 월드컵 시작을 위해 월드컵에 사용될 Candidate들을 요청하는 메서드<p>
     * 비밀번호를 입력받아야 하므로 POST 를 사용하였음
     */
    @PostMapping("/worldcups/candidates")
    public ResponseEntity postForGameStart(@RequestBody WorldCupRequestDto worldCupRequestDto) {
        Long worldCupId = worldCupRequestDto.getWorldCupId();
        Integer teamCount = worldCupRequestDto.getTeamCount();
        String password = worldCupRequestDto.getPassword();

        worldCupService.verifyPassword(worldCupId, password);
        List<CandidateSimpleResponseDto> responseDtos = candidateService.findRandomCandidates(worldCupId, teamCount);

        return ResponseEntity.ok(responseDtos);
    }
}
