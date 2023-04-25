package com.mytypeworldcup.mytypeworldcup.domain.candidate.repository;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;

import java.util.List;

public interface CandidateRepositoryCustom {
    List<CandidateSimpleResponseDto> findByIdWithTeamCount(Long worldCupId, Integer teamCount);
}
