package com.mytypeworldcup.mytypeworldcup.domain.worldcup.controller;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.service.CandidateService;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.service.WorldCupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorldCupController {
    private final WorldCupService worldCupService;
    private final CandidateService candidateService;

    @PostMapping("/worldcups")
    public ResponseEntity postWorldCup(Authentication authentication,
                                       @RequestBody @Valid WorldCupPostDto worldCupPostDto) {
        authentication.getName(); // Todo 회원이름을 어떻게 설정해줄것인가 ??

        WorldCupResponseDto worldCupResponseDto = worldCupService.createWorldCup(worldCupPostDto);

        worldCupPostDto.setWorldCupIdForCandidatePostDtos(worldCupResponseDto.getId());
        List<CandidateResponseDto> candidateResponseDtos = candidateService.createCandidates(worldCupPostDto.getCandidatePostDtos());

        worldCupResponseDto.setCandidateResponseDtos(candidateResponseDtos);


        return new ResponseEntity(worldCupResponseDto, HttpStatus.CREATED);
    }
}
