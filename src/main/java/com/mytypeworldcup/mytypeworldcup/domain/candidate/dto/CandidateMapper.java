package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    Candidate candidatePostDtoToCandidate(CandidatePostDto candidatePostDto);

    CandidateResponseDto candidateToCandidateResponseDto(Candidate candidate);

    List<CandidateResponseDto> candidatesToCandidateResponseDtos(List<Candidate> candidates);
}
