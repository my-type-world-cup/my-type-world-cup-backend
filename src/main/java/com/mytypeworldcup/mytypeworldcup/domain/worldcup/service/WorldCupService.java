package com.mytypeworldcup.mytypeworldcup.domain.worldcup.service;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupMapper;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.repository.WorldCupRepository;
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
    public Page<WorldCupSimpleResponseDto> searchWorldCups(Pageable pageable, String keyword) {
        return worldCupRepository.getWorldCupsWithCandidates(pageable, keyword);
    }

}
