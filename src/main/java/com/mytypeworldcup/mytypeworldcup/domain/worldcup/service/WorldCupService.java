package com.mytypeworldcup.mytypeworldcup.domain.worldcup.service;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupPostDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WorldCupService {
    public WorldCupResponseDto createWorldCup(WorldCupPostDto worldCupPostDto) {
        return null;
    }
}
