package com.mytypeworldcup.mytypeworldcup.domain.candidate.service;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateMapper;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidatePostDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.repository.CandidateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {
    @InjectMocks
    private CandidateService candidateService;

    @Mock
    private CandidateMapper candidateMapper;
    @Mock
    private CandidateRepository candidateRepository;

    @Test
    @DisplayName("후보 등록")
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
    @DisplayName("후보 단체 등록")
    void createCandidates() {
        // given
        List<CandidatePostDto> candidatePostDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            candidatePostDtos.add(CandidatePostDto.builder().build());
        }

        given(candidateMapper.candidatePostDtoToCandidate(any(CandidatePostDto.class))).willReturn(new Candidate());
        given(candidateRepository.save(any(Candidate.class))).willReturn(new Candidate());
        given(candidateMapper.candidateToCandidateResponseDto(any(Candidate.class))).willReturn(CandidateResponseDto.builder().build());

        // when
        List<CandidateResponseDto> actual = candidateService.createCandidates(candidatePostDtos);

        // then
        verify(candidateMapper, times(candidatePostDtos.size())).candidatePostDtoToCandidate(any(CandidatePostDto.class));
        verify(candidateRepository, times(candidatePostDtos.size())).save(any(Candidate.class));
        verify(candidateMapper, times(candidatePostDtos.size())).candidateToCandidateResponseDto(any(Candidate.class));
        assertEquals(candidatePostDtos.size(), actual.size());
    }

    @Test
    @DisplayName("랜덤후보 뽑아오기 - 요청한 갯수만큼 리턴하는지 검증")
    void findRandomCandidates() {
        // 결과가 teamCount 만큼 왔는지 검증
        // given
        Long worldCupId = 1L;
        Integer teamCount = 16;

        List<CandidateSimpleResponseDto> expected = new ArrayList<>();
        for (int i = 0; i < teamCount; i++) {
            expected.add(new CandidateSimpleResponseDto());
        }

        given(candidateRepository.findRandomCandidatesByWorldCupIdLimitTeamCount(anyLong(), anyInt())).willReturn(expected);

        // when
        List<CandidateSimpleResponseDto> actual = candidateService.findRandomCandidates(worldCupId, teamCount);

        // then
        assertEquals(expected.size(), actual.size());
    }

    @Test
    void updateMatchResult() {
    }

    @Test
    void updateMatchResults() {
        // 이건 어떻게 해야하나 ...
    }

    @Test
    void findCandidatesByWorldCupId() {
    }
}