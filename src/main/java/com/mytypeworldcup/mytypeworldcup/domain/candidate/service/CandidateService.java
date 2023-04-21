package com.mytypeworldcup.mytypeworldcup.domain.candidate.service;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidatePostDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CandidateService {
    public CandidateResponseDto createCandidate(CandidatePostDto candidatePostDto) {
        return null;
    }

    public List<CandidateResponseDto> createCandidates(List<CandidatePostDto> candidatePostDtos) {
        return candidatePostDtos.stream()
                .map(this::createCandidate)
                .toList();
    }

}
