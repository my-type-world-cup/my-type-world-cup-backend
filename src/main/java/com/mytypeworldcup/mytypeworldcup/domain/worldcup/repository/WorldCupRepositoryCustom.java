package com.mytypeworldcup.mytypeworldcup.domain.worldcup.repository;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupSimpleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorldCupRepositoryCustom {
    Page<WorldCupSimpleResponseDto> getWorldCupsWithCandidates(Long memberId, String keyword, Pageable pageable);
}
