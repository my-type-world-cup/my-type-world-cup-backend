package com.mytypeworldcup.mytypeworldcup.domain.worldcup.repository;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.QCandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.QWorldCupSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupSimpleResponseDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import static com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.QWorldCup.worldCup;

@RequiredArgsConstructor
public class WorldCupRepositoryCustomImpl implements WorldCupRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<WorldCupSimpleResponseDto> getWorldCupsWithCandidates(Pageable pageable, String keyword) {
        // 메인 쿼리
        JPAQuery<WorldCupSimpleResponseDto> query = queryFactory
                .select(new QWorldCupSimpleResponseDto(
                        worldCup.id,
                        worldCup.title,
                        worldCup.description))
                .from(worldCup)
                .where(
                        containsKeyword(keyword)
                                .and(worldCup.visibility.eq(true))
                );

        // 정렬 조건 세팅 및 패치
        List<WorldCupSimpleResponseDto> result = query
                .orderBy(getOrderSpecifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // candidate 세팅
        result.stream()
                .forEach(worldCupSimpleResponseDto -> {
                    List<CandidateSimpleResponseDto> fetch = queryFactory
                            .select(new QCandidateSimpleResponseDto(
                                    candidate.id,
                                    candidate.name,
                                    candidate.image))
                            .from(candidate)
                            .where(candidate.worldCup.id.eq(worldCupSimpleResponseDto.getId()))
                            .orderBy(candidate.finalWinCount.desc())
                            .limit(2)
                            .fetch();
                    worldCupSimpleResponseDto.setCandidateSimpleResponseDtos(fetch);
                });

        return PageableExecutionUtils.getPage(result, pageable, query::fetchCount);
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        return worldCup.title.contains(keyword)
                .or(worldCup.description.contains(keyword));
    }

    private OrderSpecifier getOrderSpecifier(Pageable pageable) {
        Sort.Order direction = pageable.getSort().get().collect(Collectors.toList()).get(0);

        Order order = direction.getDirection().isAscending()
                ? Order.ASC
                : Order.DESC;

        switch (direction.getProperty()) {
            case "createdAt":
                return new OrderSpecifier(order, worldCup.createdAt);
            default: // "playCount"
                return new OrderSpecifier(order, worldCup.playCount);
        }
    }

}