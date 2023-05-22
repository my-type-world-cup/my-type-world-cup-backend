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

    public WorldCupResponseDto updateWorldCup(long worldCupId, WorldCupPatchDto worldCupPatchDto) {
        WorldCup worldCup = findVerifiedWorldCup(worldCupId);

        Optional.ofNullable(worldCupPatchDto.getTitle()).ifPresent(data -> worldCup.updateTitle(data));
        Optional.ofNullable(worldCupPatchDto.getDescription()).ifPresent(data -> worldCup.updateDescription(data));
        Optional.ofNullable(worldCupPatchDto.getPassword()).ifPresent(data -> {
            if (data.equals("null")) {
                worldCup.updatePassword(null);
            } else {
                worldCup.updatePassword(data);
            }
        });

        return worldCupMapper.worldCupToWorldCupResponseDto(worldCup);
    }

    @Transactional(readOnly = true)
    public Page<WorldCupSimpleResponseDto> searchWorldCups(Long memberId, String keyword, Pageable pageable) {
        return worldCupRepository.getWorldCupsWithCandidates(memberId, keyword, pageable);
    }

    @Transactional(readOnly = true)
    public WorldCupPreview findWorldCupPreview(long worldCupId) {
        WorldCup worldCup = findVerifiedWorldCup(worldCupId);
        return worldCupMapper.worldCupToWorldCupPreview(worldCup);
    }

    @Transactional(readOnly = true)
    public WorldCupResponseDto findWorldCupDetails(long worldCupId) {
        WorldCup worldCup = findVerifiedWorldCup(worldCupId);
        return worldCupMapper.worldCupToWorldCupResponseDto(worldCup);
    }

    @Transactional(readOnly = true)
    public void verifyPassword(Long worldCupId, String password) {
        String worldCupPassword = findVerifiedWorldCup(worldCupId).getPassword();
        if (worldCupPassword != null && !worldCupPassword.equals(password)) {
            throw new BusinessLogicException(CommonExceptionCode.INVALID_PASSWORD);
        }
    }

    @Transactional(readOnly = true)
    public void verifyWorldCupAccess(String email, long worldCupId) {
        WorldCup worldCup = findVerifiedWorldCup(worldCupId);
        if (!worldCup.getMember().getEmail().equals(email)) {
            throw new BusinessLogicException(CommonExceptionCode.FORBIDDEN);
        }
    }

    @Transactional(readOnly = true)
    public WorldCup findVerifiedWorldCup(long worldCupId) {
        return worldCupRepository.findById(worldCupId)
                .orElseThrow(() -> new BusinessLogicException(WorldCupExceptionCode.WORLD_CUP_NOT_FOUND));
    }
}
