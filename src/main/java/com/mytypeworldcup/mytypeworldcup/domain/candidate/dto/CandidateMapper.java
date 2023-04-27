package com.mytypeworldcup.mytypeworldcup.domain.candidate.dto;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    Candidate candidatePostDtoToCandidate(CandidatePostDto candidatePostDto);

    CandidateResponseDto candidateToCandidateResponseDto(Candidate candidate);

}
