package com.mytypeworldcup.mytypeworldcup.domain.worldcup.service;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.*;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.exception.WorldCupExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.repository.WorldCupRepository;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import com.mytypeworldcup.mytypeworldcup.global.error.CommonExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WorldCupService {

    private final WorldCupMapper worldCupMapper;
    private final WorldCupRepository worldCupRepository;

    public WorldCupResponseDto createWorldCup(WorldCupPostDto worldCupPostDto) {
        // PostDto -> WorldCup
        WorldCup worldCup = worldCupMapper.worldCupPostDtoToWorldCup(worldCupPostDto);

        // WorldCup Save
        WorldCup savedWorldCup = worldCupRepository.save(worldCup);

        // WorldCup -> ResponseDto
        return worldCupMapper.worldCupToWorldCupResponseDto(savedWorldCup);
    }

    @Transactional(readOnly = true)
    public Page<WorldCupSimpleResponseDto> searchWorldCups(Long memberId, String keyword, Pageable pageable) {
        return worldCupRepository.getWorldCupsWithCandidates(memberId, keyword, pageable);
    }

    @Transactional(readOnly = true)
    public GetWorldCupResponseDto findWorldCup(long worldCupId) {
        WorldCup worldCup = findVerifiedWorldCup(worldCupId);
        return worldCupMapper.worldCupToGetWorldCupResponseDto(worldCup);
    }

    @Transactional(readOnly = true)
    public void verifyPassword(Long worldCupId, String password) {
        WorldCup worldCup = findVerifiedWorldCup(worldCupId);
        if (worldCup.getPassword() != password) {
            throw new BusinessLogicException(CommonExceptionCode.UNAUTHORIZED);
        }
    }

    @Transactional(readOnly = true)
    private WorldCup findVerifiedWorldCup(long worldCupId) {
        return worldCupRepository.findById(worldCupId)
                .orElseThrow(() -> new BusinessLogicException(WorldCupExceptionCode.WORLD_CUP_NOT_FOUND));
    }
}
