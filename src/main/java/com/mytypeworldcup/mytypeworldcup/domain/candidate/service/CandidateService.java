package com.mytypeworldcup.mytypeworldcup.domain.candidate.service;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateMapper;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidatePostDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateMapper candidateMapper;
    private final CandidateRepository candidateRepository;

    public CandidateResponseDto createCandidate(CandidatePostDto candidatePostDto) {
        // PostDto -> Candidate
        Candidate candidate = candidateMapper.candidatePostDtoToCandidate(candidatePostDto);

        // Candidate 저장
        Candidate savedCandidate = candidateRepository.save(candidate);

        // 저장된 Candidate -> ResponseDto 변환 후 리턴
        return candidateMapper.candidateToCandidateResponseDto(savedCandidate);
    }

    public List<CandidateResponseDto> createCandidates(List<CandidatePostDto> candidatePostDtos) {
        return candidatePostDtos.stream()
                .map(this::createCandidate)
                .toList();
    }

    public List<CandidateSimpleResponseDto> findCandidatesForGameStart(Long worldCupId, Integer teamCount) {
        return candidateRepository.findByIdWithTeamCount(worldCupId, teamCount);
    }
}
