package com.mytypeworldcup.mytypeworldcup.domain.worldcup.controller;

import com.mytypeworldcup.mytypeworldcup.domain.member.service.MemberService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupPatchDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import com.mytypeworldcup.mytypeworldcup.global.common.PageResponseDto;
import com.mytypeworldcup.mytypeworldcup.global.common.SearchRequestParamDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Validated
@RestController
@RequiredArgsConstructor
public class WorldCupController {
    private final WorldCupService worldCupService;
    private final MemberService memberService;

    @PostMapping("/worldcups")
    public ResponseEntity postWorldCup(Authentication authentication,
                                       @RequestBody @Valid WorldCupPostDto worldCupPostDto) {
        // 멤버아이디 설정 -> jwt토큰의 이메일을 기반으로 멤버아이디를 검색
        Long memberId = memberService.findMemberIdByEmail(authentication.getName());
        worldCupPostDto.setMemberId(memberId);

        // WorldCup 생성
        WorldCupResponseDto worldCupResponseDto = worldCupService.createWorldCup(worldCupPostDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(worldCupResponseDto);
    }

    @PatchMapping("/worldcups/{worldCupId}")
    public ResponseEntity patchWorldCup(Authentication authentication,
                                        @Positive @PathVariable long worldCupId,
                                        @RequestBody @Valid WorldCupPatchDto worldCupPatchDto) {

        worldCupService.verifyWorldCupAccess(authentication.getName(), worldCupId);
        WorldCupResponseDto worldCupResponseDto = worldCupService.updateWorldCup(worldCupId, worldCupPatchDto);

        return ResponseEntity.ok(worldCupResponseDto);
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
    public ResponseEntity getWorldCups(@ModelAttribute @Valid SearchRequestParamDto params) {

        PageRequest pageRequest = PageRequest.of(params.getPage(), params.getSize(), params.getDirection(), params.getSort());
        Page<WorldCupSimpleResponseDto> responseDtos = worldCupService.searchWorldCups(null, params.getKeyword(), pageRequest);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(30L)))
                .body(new PageResponseDto(responseDtos));
    }

    /**
     * 월드컵 미리보기
     */
    @GetMapping("/worldcups/{worldCupId}")
    public ResponseEntity getWorldCupPreview(@Positive @PathVariable long worldCupId) {
        return ResponseEntity.ok(worldCupService.findWorldCupPreview(worldCupId));
    }

    /**
     * 월드컵 상세보기(월드컵 생성자만 가능)
     */
    @GetMapping("/worldcups/{worldCupId}/details")
    public ResponseEntity getWorldCupDetails(Authentication authentication,
                                             @Positive @PathVariable long worldCupId) {
        worldCupService.verifyWorldCupAccess(authentication.getName(), worldCupId);
        WorldCupResponseDto worldCupResponseDto = worldCupService.findWorldCupDetails(worldCupId);
        return ResponseEntity.ok(worldCupResponseDto);
    }

    @GetMapping("/members/worldcups")
    public ResponseEntity getMyWorldCups(Authentication authentication,
                                         @ModelAttribute @Valid SearchRequestParamDto params) {
        Long memberId = memberService.findMemberIdByEmail(authentication.getName());

        PageRequest pageRequest = PageRequest.of(params.getPage(), params.getSize(), params.getDirection(), params.getSort());
        Page<WorldCupSimpleResponseDto> responseDtos = worldCupService.searchWorldCups(memberId, params.getKeyword(), pageRequest);

        return ResponseEntity.ok(new PageResponseDto(responseDtos));
    }
}
