package com.mytypeworldcup.mytypeworldcup.domain.candidate.repository;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CandidateRepositoryCustom {
    List<CandidateSimpleResponseDto> findRandomCandidatesByWorldCupIdLimitTeamCount(Long worldCupId, Integer teamCount);

    Page<CandidateResponseDto> searchAllByWorldCupId(Long worldCupId, String keyword, Pageable pageable);
}
