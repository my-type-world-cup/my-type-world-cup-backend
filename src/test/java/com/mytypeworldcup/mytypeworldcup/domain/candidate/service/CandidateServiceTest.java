package com.mytypeworldcup.mytypeworldcup.domain.candidate.service;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.*;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.exception.CandidateExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.repository.CandidateRepository;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    @DisplayName("경기결과 업데이트 - 유효한 후보가 없을 경우")
    void updateMatchResult_CANDIDATE_NOT_FOUND() {
        // given
        CandidatePatchDto candidatePatchDto = CandidatePatchDto
                .builder()
                .id(1L)
                .build();

        given(candidateRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when
        BusinessLogicException actual = assertThrows(BusinessLogicException.class, () -> candidateService.updateMatchResult(candidatePatchDto));

        // then
        assertEquals(CandidateExceptionCode.CANDIDATE_NOT_FOUND, actual.getExceptionCode());
    }

    @Test
    @DisplayName("경기결과 업데이트 - 최종우승하지 못한 경우")
    void updateMatchResult_notFinalWin() {
        // given
        CandidatePatchDto candidatePatchDto = CandidatePatchDto
                .builder()
                .id(1L)
                .matchUpGameCount(4)
                .winCount(3)
                .build();

        WorldCup worldCup = new WorldCup();

        Candidate candidate = Candidate
                .builder()
                .worldCup(worldCup)
                .build();

        given(candidateRepository.findById(anyLong())).willReturn(Optional.ofNullable(candidate));

        // when
        candidateService.updateMatchResult(candidatePatchDto);

        // then
        assertEquals(1, candidate.getMatchUpWorldCupCount()); // 월드컵에 출전한 횟수이므로 무조건 1증가
        assertEquals(candidatePatchDto.getMatchUpGameCount(), candidate.getMatchUpGameCount());
        assertEquals(candidatePatchDto.getWinCount(), candidate.getWinCount());
        // 최종우승하지 못했으므로 if문 동작하지 않으므로 0
        assertEquals(0, candidate.getFinalWinCount());
        assertEquals(0, worldCup.getPlayCount());
    }

    @Test
    @DisplayName("경기결과 업데이트 - 최종우승한 경우")
    void updateMatchResult_finalWin() {
        // given
        CandidatePatchDto candidatePatchDto = CandidatePatchDto
                .builder()
                .id(1L)
                .matchUpGameCount(4)
                .winCount(4)
                .build();

        WorldCup worldCup = new WorldCup();

        Candidate candidate = Candidate
                .builder()
                .worldCup(worldCup)
                .build();

        given(candidateRepository.findById(anyLong())).willReturn(Optional.ofNullable(candidate));

        // when
        candidateService.updateMatchResult(candidatePatchDto);

        // then
        assertEquals(1, candidate.getMatchUpWorldCupCount()); // 월드컵에 출전한 횟수이므로 무조건 1증가
        assertEquals(candidatePatchDto.getMatchUpGameCount(), candidate.getMatchUpGameCount());
        assertEquals(candidatePatchDto.getWinCount(), candidate.getWinCount());
        assertEquals(1, candidate.getFinalWinCount()); // 최종우승여부이므로 무조건 1증가
        assertEquals(1, worldCup.getPlayCount()); // 플레이된 횟수이므로 1증가
    }

    @Test
    @DisplayName("경기결과 리스트 업데이트")
    void updateMatchResults() {
        // given
        List<CandidatePatchDto> candidatePatchDtos = new ArrayList<>();
        for (long i = 1; i <= 4; i++) {
            CandidatePatchDto candidatePatchDto = CandidatePatchDto
                    .builder()
                    .id(i)
                    .winCount(1)
                    .matchUpGameCount(2)
                    .build();
            candidatePatchDtos.add(candidatePatchDto);
        }

        given(candidateRepository.findById(anyLong())).willReturn(Optional.ofNullable(new Candidate()));

        // when
        candidateService.updateMatchResults(candidatePatchDtos);

        // then
        verify(candidateRepository, times(candidatePatchDtos.size())).findById(anyLong());
    }

    @Test
    void findCandidatesByWorldCupId() {
    }
}