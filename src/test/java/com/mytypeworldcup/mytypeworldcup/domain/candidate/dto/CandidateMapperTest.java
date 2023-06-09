package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CandidateMapperTest {
    @Spy
    private CandidateMapper candidateMapper = Mappers.getMapper(CandidateMapper.class);

    @Test
    @DisplayName("CandidatePostDto -> Candidate")
    void candidatePostDtoToCandidate() {
        // given
        CandidatePostDto candidatePostDto = CandidatePostDto
                .builder()
                .name("테스트")
                .image("이미지url")
                .thumb("썸네일url")
                .worldCupId(1L)
                .build();

        // when
        Candidate candidate = candidateMapper.candidatePostDtoToCandidate(candidatePostDto);

        // then
        assertEquals(candidatePostDto.getName(), candidate.getName());
        assertEquals(candidatePostDto.getImage(), candidate.getImage());
        assertEquals(candidatePostDto.getThumb(), candidate.getThumb());
        assertEquals(candidatePostDto.getWorldCupId(), candidate.getWorldCupId());
    }

    @Test
    @DisplayName("Candidate -> CandidateResponseDto")
    void candidateToCandidateResponseDto() throws NoSuchFieldException, IllegalAccessException {
        // given
        WorldCup worldCup = new WorldCup();
        worldCup.setId(4L);

        Candidate candidate = Candidate
                .builder()
                .name("테스트")
                .image("이미지url")
                .thumb("썸네일url")
                .worldCup(worldCup)
                .build();
        // candidate id 설정
        Field idField = candidate.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(candidate, 3L);

        // 오타 방지를 위해 모두 다르게 값을 설정
        candidate.updateFinalWinCount();
        candidate.updateWinCount(10);
        candidate.updateMatchUpWorldCupCount();
        candidate.updateMatchUpWorldCupCount(); // 2회로 설정
        candidate.updateMatchUpGameCount(7);

        // when
        CandidateResponseDto candidateResponseDto = candidateMapper.candidateToCandidateResponseDto(candidate);

        // then
        assertEquals(candidate.getId(), candidateResponseDto.getId());
        assertEquals(candidate.getName(), candidateResponseDto.getName());
        assertEquals(candidate.getImage(), candidateResponseDto.getImage());
        assertEquals(candidate.getThumb(), candidateResponseDto.getThumb());
        assertEquals(candidate.getFinalWinCount(), candidateResponseDto.getFinalWinCount());
        assertEquals(candidate.getWinCount(), candidateResponseDto.getWinCount());
        assertEquals(candidate.getMatchUpWorldCupCount(), candidateResponseDto.getMatchUpWorldCupCount());
        assertEquals(candidate.getMatchUpGameCount(), candidateResponseDto.getMatchUpGameCount());
        assertEquals(candidate.getWorldCupId(), candidateResponseDto.getWorldCupId());
    }
}