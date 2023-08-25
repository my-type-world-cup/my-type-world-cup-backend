package com.mytypeworldcup.mytypeworldcup.domain.worldcup.service;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.*;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorldCupService {
    WorldCupResponseDto createWorldCup(WorldCupPostDto worldCupPostDto);

    WorldCupResponseDto updateWorldCup(long worldCupId, WorldCupPatchDto worldCupPatchDto);

    Page<WorldCupSimpleResponseDto> searchWorldCups(Long memberId, String keyword, Pageable pageable);

    WorldCupPreview findWorldCupPreview(long worldCupId);

    WorldCupResponseDto findWorldCupDetails(long worldCupId);

    void verifyPassword(Long worldCupId, String password);

    void verifyWorldCupAccess(String email, long worldCupId);

    WorldCup findVerifiedWorldCup(long worldCupId);

    void deleteWorldCup(long worldCupId);

}