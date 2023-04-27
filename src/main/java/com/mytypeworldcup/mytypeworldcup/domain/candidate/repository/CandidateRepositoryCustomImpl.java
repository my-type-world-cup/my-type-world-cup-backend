package com.mytypeworldcup.mytypeworldcup.domain.candidate.repository;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.QCandidateResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.QCandidateSimpleResponseDto;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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
        Expression sort = propertyToSortExpression(sortOrder.getProperty());

        return new OrderSpecifier(order, sort);
    }

    private Expression propertyToSortExpression(String property) {
        switch (property) {
            case "winRatio": // 1대1 승률
                return candidate.winCount.divide(candidate.matchUpGameCount);
            case "finalWinRatio": // 최종 우승 비율
                return candidate.finalWinCount.divide(candidate.matchUpWorldCupCount);
            case "name": // 이름 순
                return candidate.name;
            case "matchUpWorldCupCount": // 월드컵 참가 횟수
                return candidate.matchUpWorldCupCount;
            case "matchUpGameCount": //  1대1 매칭 횟수
                return candidate.matchUpGameCount;
            case "finalWinCount": // 최종 우승 횟수
                return candidate.finalWinCount;
            default: // 1대1 이긴 횟수
                return candidate.winCount;
        }
    }

}
