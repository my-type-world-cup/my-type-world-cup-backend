package com.mytypeworldcup.mytypeworldcup.domain.candidate.service;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CandidateService {

    CandidateResponseDto createCandidate(CandidatePostDto candidatePostDto);

    List<CandidateSimpleResponseDto> findRandomCandidates(Long worldCupId, Integer teamCount);

    void updateMatchResult(MatchDto matchDto);

    void updateMatchResults(List<MatchDto> matchDtos);

    Page<CandidateResponseDto> findCandidatesByWorldCupId(Long worldCupId, String keyword, Pageable pageable);

    CandidateResponseDto updateCandidate(long candidateId, CandidatePatchDto candidatePatchDto);

    void deleteCandidate(long candidateId);

    void verifyAccess(String email, long candidateId);

}
