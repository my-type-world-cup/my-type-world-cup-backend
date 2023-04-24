package com.mytypeworldcup.mytypeworldcup.domain.worldcup.service;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.*;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.exception.WorldCupExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.repository.WorldCupRepository;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public Page<WorldCupSimpleResponseDto> searchWorldCups(Pageable pageable, String keyword) {
        return worldCupRepository.getWorldCupsWithCandidates(pageable, keyword);
    }

    @Transactional(readOnly = true)
    public GetWorldCupResponseDto findWorldCup(long worldCupId) {
        WorldCup worldCup = findVerifiedWorldCup(worldCupId);
        return worldCupMapper.worldCupToGetWorldCupResponseDto(worldCup);
    }

    @Transactional(readOnly = true)
    private WorldCup findVerifiedWorldCup(long worldCupId) {
        Optional<WorldCup> optionalWorldCup = worldCupRepository.findById(worldCupId);
        return optionalWorldCup.orElseThrow(() -> new BusinessLogicException(WorldCupExceptionCode.WORLD_CUP_NOT_FOUND));
    }

}
