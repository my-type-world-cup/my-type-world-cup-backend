package com.mytypeworldcup.mytypeworldcup.domain.candidate.repository;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.QCandidateSimpleResponseDto;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.QCandidate.candidate;

@RequiredArgsConstructor
public class CandidateRepositoryCustomImpl implements CandidateRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CandidateSimpleResponseDto> findRandomCandidatesByWorldCupIdLimitTeamCount(Long worldCupId, Integer teamCount) {
        return queryFactory
                .select(new QCandidateSimpleResponseDto(
                        candidate.id,
                        candidate.name,
                        candidate.image))
                .from(candidate)
                .where(candidate.worldCup.id.eq(worldCupId))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(teamCount)
                .fetch();
    }

}
