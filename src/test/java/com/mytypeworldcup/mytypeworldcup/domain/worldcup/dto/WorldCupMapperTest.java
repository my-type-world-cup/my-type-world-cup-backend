package com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class WorldCupMapperTest {
    @Spy
    private WorldCupMapper worldCupMapper = Mappers.getMapper(WorldCupMapper.class);

    @Test
    @DisplayName("WorldCupPostDto -> WorldCup")
    void worldCupPostDtoToWorldCup() {
        // given
        WorldCupPostDto worldCupPostDto = WorldCupPostDto
                .builder()
                .title("춘식이 월드컵")
                .description("춘식이 월드컵이에요!")
                .visibility(false)
                .password("4885")
                .build();
        worldCupPostDto.setMemberId(1L);

        // when
        WorldCup worldCup = worldCupMapper.worldCupPostDtoToWorldCup(worldCupPostDto);

        // then
        assertEquals(worldCupPostDto.getTitle(), worldCup.getTitle());
        assertEquals(worldCupPostDto.getDescription(), worldCup.getDescription());
        assertEquals(worldCupPostDto.getVisibility(), worldCup.getVisibility());
        assertEquals(worldCupPostDto.getPassword(), worldCup.getPassword());
        assertEquals(worldCupPostDto.getMemberId(), worldCup.getMemberId());
    }

    @Test
    @DisplayName("WorldCup -> WorldCupResponseDto")
    void worldCupToWorldCupResponseDto() {
        // given
        Member member = new Member();
        member.setId(1L);

        WorldCup worldCup = WorldCup
                .builder()
                .title("춘식이 월드컵")
                .description("춘식이 월드컵이에요!")
                .visibility(false)
                .password("4885")
                .member(member)
                .build();
        worldCup.setId(2L);

        // when
        WorldCupResponseDto worldCupResponseDto = worldCupMapper.worldCupToWorldCupResponseDto(worldCup);

        // then
        assertEquals(worldCup.getId(), worldCupResponseDto.getId());
        assertEquals(worldCup.getTitle(), worldCupResponseDto.getTitle());
        assertEquals(worldCup.getDescription(), worldCupResponseDto.getDescription());
        assertEquals(worldCup.getVisibility(), worldCupResponseDto.getVisibility());
        assertEquals(worldCup.getPassword(), worldCupResponseDto.getPassword());
        assertEquals(worldCup.getMemberId(), worldCupResponseDto.getMemberId());
    }

    @Test
    @DisplayName("WorldCup -> WorldCupInfoResponseDto")
    void worldCupToWorldCupInfoResponseDto() {
        // given
        WorldCup worldCup = WorldCup
                .builder()
                .title("춘식이 월드컵")
                .description("춘식이 월드컵이에요!")
                .visibility(false)
                .build();
        worldCup.setId(2L);
        List<Candidate> candidates = worldCup.getCandidates();
        for (int i = 0; i < 5; i++) {
            candidates.add(new Candidate());
        }

        // when
        WorldCupInfoResponseDto worldCupInfoResponseDto = worldCupMapper.worldCupToWorldCupInfoResponseDto(worldCup);

        // then
        assertEquals(worldCup.getId(), worldCupInfoResponseDto.getId());
        assertEquals(worldCup.getTitle(), worldCupInfoResponseDto.getTitle());
        assertEquals(worldCup.getDescription(), worldCupInfoResponseDto.getDescription());
        assertEquals(worldCup.getVisibility(), worldCupInfoResponseDto.getVisibility());
        assertEquals(worldCup.getCandidatesCount(), worldCupInfoResponseDto.getCandidatesCount());
    }
}