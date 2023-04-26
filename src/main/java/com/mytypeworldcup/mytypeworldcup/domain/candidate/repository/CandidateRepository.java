package com.mytypeworldcup.mytypeworldcup.domain.candidate.repository;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long>, CandidateRepositoryCustom {
    List<Candidate> findAllByWorldCup_Id(Long worldCupId);
}
