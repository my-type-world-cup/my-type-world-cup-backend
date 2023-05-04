package com.mytypeworldcup.mytypeworldcup.domain.candidate.service;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateMapper;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidatePostDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.repository.CandidateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {
    @InjectMocks
    private CandidateService candidateService;

    @Mock
    private CandidateMapper candidateMapper;
    @Mock
    private CandidateRepository candidateRepository;

    @Test
    void createCandidate() {
        // given
        CandidatePostDto candidatePostDto = CandidatePostDto
                .builder()
                .name("카리나")
                .image("카리나이미지링크")
                .build();
        candidatePostDto.setWorldCupId(1L);

        CandidateResponseDto expected = CandidateResponseDto
                .builder()
                .id(1L)
                .name(candidatePostDto.getName())
                .image(candidatePostDto.getImage())
                .finalWinCount(0)
                .winCount(0)
                .matchUpWorldCupCount(0)
                .matchUpGameCount(0)
                .worldCupId(candidatePostDto.getWorldCupId())
                .build();

        given(candidateMapper.candidatePostDtoToCandidate(any(CandidatePostDto.class))).willReturn(new Candidate());
        given(candidateRepository.save(any(Candidate.class))).willReturn(new Candidate());
        given(candidateMapper.candidateToCandidateResponseDto(any(Candidate.class))).willReturn(expected);

        // when
        CandidateResponseDto actual = candidateService.createCandidate(candidatePostDto);

        // then
        assertSame(expected, actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getImage(), actual.getImage());
        assertEquals(0, actual.getFinalWinCount());
        assertEquals(0, actual.getWinCount());
        assertEquals(0, actual.getMatchUpWorldCupCount());
        assertEquals(0, actual.getMatchUpGameCount());
        assertEquals(expected.getWorldCupId(), actual.getWorldCupId());
    }

    @Test
    void createCandidates() {
    }

    @Test
    void findRandomCandidates() {
    }

    @Test
    void updateMatchResults() {
    }

    @Test
    void findCandidatesByWorldCupId() {
    }
}