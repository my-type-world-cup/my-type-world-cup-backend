package com.mytypeworldcup.mytypeworldcup.domain.candidate.repository;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;

import java.util.List;

public interface CandidateRepositoryCustom {
    List<CandidateSimpleResponseDto> findRandomCandidatesByWorldCupIdLimitTeamCount(Long worldCupId, Integer teamCount);
}
