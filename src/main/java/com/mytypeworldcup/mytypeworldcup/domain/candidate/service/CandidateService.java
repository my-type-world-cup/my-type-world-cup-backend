package com.mytypeworldcup.mytypeworldcup.domain.candidate.service;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.*;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.exception.CandidateExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.repository.CandidateRepository;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateMapper candidateMapper;
    private final CandidateRepository candidateRepository;

    public CandidateResponseDto createCandidate(CandidatePostDto candidatePostDto) {
        // PostDto -> Candidate
        Candidate candidate = candidateMapper.candidatePostDtoToCandidate(candidatePostDto);

        // Candidate 저장
        Candidate savedCandidate = candidateRepository.save(candidate);

        // 저장된 Candidate -> ResponseDto 변환 후 리턴
        return candidateMapper.candidateToCandidateResponseDto(savedCandidate);
    }

    public List<CandidateResponseDto> createCandidates(List<CandidatePostDto> candidatePostDtos) {
        return candidatePostDtos.stream()
                .map(this::createCandidate)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CandidateSimpleResponseDto> findRandomCandidates(Long worldCupId, Integer teamCount) {
        return candidateRepository.findRandomCandidatesByWorldCupIdLimitTeamCount(worldCupId, teamCount);
    }

    public void updateMatchResults(List<CandidatePatchDto> candidatePatchDtos) {
        for (CandidatePatchDto candidatePatchDto : candidatePatchDtos) {

            Candidate candidate = findVerifiedCandidate(candidatePatchDto.getId());

            int matchUpGameCount = candidatePatchDto.getMatchUpGameCount();
            int winCount = candidatePatchDto.getWinCount();

            candidate.updateMatchUpWorldCupCount(); // 월드컵 출전 횟수 업데이트 : 무조건 1증가
            candidate.updateMatchUpGameCount(matchUpGameCount); // 경기 출전 횟수 업데이트
            candidate.updateWinCount(winCount); // 경기에서 승리 횟수 업데이트
            if (matchUpGameCount == winCount) { // 최종 우승 시
                candidate.updateFinalWinCount(); // finalWinCount += 1
                candidate.getWorldCup().updatePlayCount(); // worldCup.playCount += 1
            }

        }
        return;
    }

    @Transactional(readOnly = true)
    private Candidate findVerifiedCandidate(Long candidateId) {
        return candidateRepository.findById(candidateId)
                .orElseThrow(() -> new BusinessLogicException(CandidateExceptionCode.CANDIDATE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<CandidateResponseDto> findCandidatesByWorldCupId(Long worldCupId, String keyword, Pageable pageable) {
        return candidateRepository.searchAllByWorldCupId(worldCupId, keyword, pageable);
    }

}
