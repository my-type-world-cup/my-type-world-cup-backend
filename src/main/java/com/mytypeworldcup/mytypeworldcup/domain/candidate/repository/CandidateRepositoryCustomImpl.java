package com.mytypeworldcup.mytypeworldcup.domain.candidate.repository;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.QCandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.QCandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.entity.Candidate;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public Page<CandidateResponseDto> searchAllByWorldCupId(Pageable pageable, String keyword, Long worldCupId) {

        // 메인 쿼리
        JPAQuery<CandidateResponseDto> query = queryFactory
                .select(new QCandidateResponseDto(
                        candidate.id,
                        candidate.name,
                        candidate.image,
                        candidate.finalWinCount,
                        candidate.winCount,
                        candidate.matchUpWorldCupCount,
                        candidate.matchUpGameCount,
                        candidate.worldCup.id))
                .from(candidate)
                .where(
                        candidate.worldCup.id.eq(worldCupId)
                                .and(containsKeyword(keyword))
                );

        // 정렬 조건 세팅 및 패치
        List<CandidateResponseDto> result = query
                .orderBy(pageableToOrderSpecifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(result, pageable, query::fetchCount);
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        return candidate.name.contains(keyword);
    }

    // 정렬조건
    private OrderSpecifier pageableToOrderSpecifier(Pageable pageable) {
        Sort.Order sortOrder = pageable.getSort().get().collect(Collectors.toList()).get(0);

        Order order = sortOrder.getDirection().isAscending() ? Order.ASC : Order.DESC;
        PathBuilder<Object> sort = new PathBuilder<>(Candidate.class, "candidate").get(sortOrder.getProperty());

        return new OrderSpecifier(order, sort);
    }
}
