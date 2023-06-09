package com.mytypeworldcup.mytypeworldcup.domain.worldcup.repository;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.QCandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.QWorldCupSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.WorldCupSimpleResponseDto;
import com.querydsl.core.types.Expression;
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

    /**
     * <p>월드컵 검색 메서드 : 월드컵과 월드컵에 속한 후보 2명 함께 리턴</p>
     *
     * <p>memberId == null 일 경우</p>
     * <li>검색에서 후보 숫자 4미만은 제외될 것</li>
     * <li>비밀번호 없는 월드컵만 검색될 것</li>
     */
    @Override
    public Page<WorldCupSimpleResponseDto> getWorldCupsWithCandidates(Long memberId, String keyword, Pageable pageable) {
        //TODO 추후에 dto 새로 만들어서 한번에 받아오는거 실험해보자...!
        // 메인 쿼리
        JPAQuery<WorldCupSimpleResponseDto> query = queryFactory
                .select(new QWorldCupSimpleResponseDto(
                        worldCup.id,
                        worldCup.title,
                        worldCup.description))
                .from(worldCup)
                .where(
                        buildWorldCupSearchPredicateByMemberId(memberId)
                                .and(containsKeyword(keyword))
                );

        // 정렬 조건 세팅 및 패치
        List<WorldCupSimpleResponseDto> result = query
                .orderBy(pageableToOrderSpecifier(pageable))
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
                                    candidate.image,
                                    candidate.thumb))
                            .from(candidate)
                            .where(candidate.worldCup.id.eq(worldCupSimpleResponseDto.getId()))
                            .orderBy(candidate.finalWinCount.desc())
                            .limit(2)
                            .fetch();
                    worldCupSimpleResponseDto.setCandidateSimpleResponseDtos(fetch);
                });

        return PageableExecutionUtils.getPage(result, pageable, query::fetchCount);
    }

    // where 절 조건
    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        return worldCup.title.contains(keyword)
                .or(worldCup.description.contains(keyword));
    }

    private BooleanExpression buildWorldCupSearchPredicateByMemberId(Long memberId) {
        return memberId == null
                // memberId가 null일 경우
                ? worldCup.password.isNull() // 비밀번호 없음
                .and(worldCup.candidates.size().goe(4)) // candidates.size() 4이상

                // memberId가 null이 아닐 경우
                : worldCup.member.id.eq(memberId); // 입력받은 memberId에 속한 월드컵만 검색
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
            case "commentCount":
                return worldCup.comments.size();
            case "createdAt":
                return worldCup.createdAt;
            default: // playCount
                return worldCup.playCount;
        }
    }
}