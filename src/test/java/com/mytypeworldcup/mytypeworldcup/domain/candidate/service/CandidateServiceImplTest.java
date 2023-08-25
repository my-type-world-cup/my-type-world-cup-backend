package com.mytypeworldcup.mytypeworldcup.domain.candidate.service;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.*;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.exception.CandidateExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.repository.CandidateRepository;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import com.mytypeworldcup.mytypeworldcup.global.error.CommonExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {
    @InjectMocks
    private CandidateServiceImpl candidateService;

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
                .thumb("카리나썸네일링크")
                .worldCupId(1L)
                .build();

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
        assertEquals(expected.getThumb(), actual.getThumb());
        assertEquals(0, actual.getFinalWinCount());
        assertEquals(0, actual.getWinCount());
        assertEquals(0, actual.getMatchUpWorldCupCount());
        assertEquals(0, actual.getMatchUpGameCount());
        assertEquals(expected.getWorldCupId(), actual.getWorldCupId());
    }

    /*
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
    */
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
        MatchDto matchDto = MatchDto
                .builder()
                .id(1L)
                .build();

        given(candidateRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when
        BusinessLogicException actual = assertThrows(BusinessLogicException.class, () -> candidateService.updateMatchResult(matchDto));

        // then
        assertEquals(CandidateExceptionCode.CANDIDATE_NOT_FOUND, actual.getExceptionCode());
    }

    @Test
    @DisplayName("경기결과 업데이트 - 최종우승하지 못한 경우")
    void updateMatchResult_notFinalWin() {
        // given
        MatchDto matchDto = MatchDto
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
        candidateService.updateMatchResult(matchDto);

        // then
        assertEquals(1, candidate.getMatchUpWorldCupCount()); // 월드컵에 출전한 횟수이므로 무조건 1증가
        assertEquals(matchDto.getMatchUpGameCount(), candidate.getMatchUpGameCount());
        assertEquals(matchDto.getWinCount(), candidate.getWinCount());
        // 최종우승하지 못했으므로 if문 동작하지 않으므로 0
        assertEquals(0, candidate.getFinalWinCount());
        assertEquals(0, worldCup.getPlayCount());
    }

    @Test
    @DisplayName("경기결과 업데이트 - 최종우승한 경우")
    void updateMatchResult() {
        // given
        MatchDto matchDto = MatchDto
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
        candidateService.updateMatchResult(matchDto);

        // then
        assertEquals(1, candidate.getMatchUpWorldCupCount()); // 월드컵에 출전한 횟수이므로 무조건 1증가
        assertEquals(matchDto.getMatchUpGameCount(), candidate.getMatchUpGameCount());
        assertEquals(matchDto.getWinCount(), candidate.getWinCount());
        assertEquals(1, candidate.getFinalWinCount()); // 최종우승여부이므로 무조건 1증가
        assertEquals(1, worldCup.getPlayCount()); // 플레이된 횟수이므로 1증가
    }

    @Test
    @DisplayName("경기결과 리스트 업데이트")
    void updateMatchResults() {
        // given
        List<MatchDto> matchDtos = new ArrayList<>();
        for (long i = 1; i <= 4; i++) {
            MatchDto matchDto = MatchDto
                    .builder()
                    .id(i)
                    .winCount(1)
                    .matchUpGameCount(2)
                    .build();
            matchDtos.add(matchDto);
        }

        given(candidateRepository.findById(anyLong())).willReturn(Optional.ofNullable(new Candidate()));

        // when
        candidateService.updateMatchResults(matchDtos);

        // then
        verify(candidateRepository, times(matchDtos.size())).findById(anyLong());
    }

    @Test
    @DisplayName("월드컵아이디로 후보찾기")
    void findCandidatesByWorldCupId() {
        // given
        Long worldCupId = 1L;
        String keyword = "테스트";
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "winCount");

        List<CandidateResponseDto> candidateResponseDtos = new ArrayList<>();
        for (long i = 5; i >= 1; i--) {
            CandidateResponseDto candidateResponseDto = CandidateResponseDto
                    .builder()
                    .id(i)
                    .name("테스트" + i)
                    .image("이미지url" + i)
                    .thumb("썸네일url" + i)
                    .finalWinCount(0)
                    .winCount((int) i)
                    .matchUpWorldCupCount(5)
                    .matchUpGameCount((int) i)
                    .worldCupId(worldCupId)
                    .build();
            candidateResponseDtos.add(candidateResponseDto);
        }

        Page<CandidateResponseDto> expected = new PageImpl<>(candidateResponseDtos);

        given(candidateRepository.searchAllByWorldCupId(anyLong(), anyString(), any(Pageable.class))).willReturn(expected);

        // when
        Page<CandidateResponseDto> actual = candidateService.findCandidatesByWorldCupId(worldCupId, keyword, pageable);

        // then
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
        assertEquals(expected.getContent(), actual.getContent());
        assertSame(expected, actual);
    }

    @Test
    @DisplayName("후보삭제")
    void deleteCandidate() {
        // given
        long candidateId = 1L;
        Candidate candidate = new Candidate();
        given(candidateRepository.findById(anyLong())).willReturn(Optional.ofNullable(candidate));

        // when
        candidateService.deleteCandidate(candidateId);

        // then
        verify(candidateRepository).delete(candidate);
    }

    @Test
    @DisplayName("후보 수정")
    void updateCandidate() {
        // given
        long candidateId = 1L;
        Candidate candidate = Candidate.builder()
                .name("테스트")
                .image("이미지 url")
                .thumb("썸네일 url")
                .build();

        CandidatePatchDto candidatePatchDto = CandidatePatchDto.builder()
                .name("새로운 후보명")
                .image("새로운 이미지 url")
                .thumb("새로운 썸네일 url")
                .build();

        given(candidateRepository.findById(candidateId)).willReturn(Optional.ofNullable(candidate));

        // when
        candidateService.updateCandidate(candidateId, candidatePatchDto);

        // then
        assertEquals(candidatePatchDto.getName(), candidate.getName());
        assertEquals(candidatePatchDto.getImage(), candidate.getImage());
        assertEquals(candidatePatchDto.getThumb(), candidate.getThumb());
        verify(candidateMapper).candidateToCandidateResponseDto(candidate);
    }

    @Test
    @DisplayName("접근권한 검증 - 성공")
    void verifyAccess() {
        // given
        String email = "test@test.com";
        long candidateId = 1L;
        Member member = Member.builder().email("test@test.com").build();
        WorldCup worldCup = WorldCup.builder().member(member).build();
        Candidate candidate = Candidate.builder().worldCup(worldCup).build();

        given(candidateRepository.findById(candidateId)).willReturn(Optional.ofNullable(candidate));

        // when
        // then
        candidateService.verifyAccess(email, candidateId);
    }

    @Test
    @DisplayName("접근권한 검증 - 실패")
    void verifyAccess_bad() {
        // given
        String email = "badtest@test.com";
        long candidateId = 1L;
        Member member = Member.builder().email("test@test.com").build();
        WorldCup worldCup = WorldCup.builder().member(member).build();
        Candidate candidate = Candidate.builder().worldCup(worldCup).build();

        given(candidateRepository.findById(candidateId)).willReturn(Optional.ofNullable(candidate));

        // when
        BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> candidateService.verifyAccess(email, candidateId));

        // then
        assertEquals(CommonExceptionCode.FORBIDDEN, thrown.getExceptionCode());
    }
}