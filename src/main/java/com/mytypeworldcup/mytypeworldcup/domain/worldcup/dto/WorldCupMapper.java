package com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto;

import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorldCupMapper {

    WorldCup worldCupPostDtoToWorldCup(WorldCupPostDto worldCupPostDto);

    WorldCupResponseDto worldCupToWorldCupResponseDto(WorldCup worldCup);

    WorldCupInfoResponseDto worldCupToWorldCupInfoResponseDto(WorldCup worldCup);
}
