package com.mytypeworldcup.mytypeworldcup.domain.comment.repository;

import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.CommentResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.comment.dto.QCommentResponseDto;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.mytypeworldcup.mytypeworldcup.domain.comment.entity.QComment.comment;
import static com.mytypeworldcup.mytypeworldcup.domain.like.entity.QLike.like;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentResponseDto> findAllByWorldCupId(Long worldCupId, Long memberId, Pageable pageable) {
        // 메인 쿼리
        JPAQuery<CommentResponseDto> query = queryFactory
                .select(new QCommentResponseDto(
                        comment.id,
                        comment.content,
                        comment.candidateName,
                        comment.likes.size(),
                        isLiked(memberId),
                        comment.createdAt,
                        comment.modifiedAt,
                        comment.member.id,
                        comment.member.nickname,
                        comment.worldCup.id))
                .from(comment)
                .leftJoin(comment.member)
                .where(comment.worldCup.id.eq(worldCupId));

        // 정렬 조건 세팅 및 패치
        List<CommentResponseDto> result = query
                .orderBy(pageableToOrderSpecifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(result, pageable, query::fetchCount);
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
            case "createdAt":
                return comment.createdAt;
            default: // likesCount
                return comment.likes.size();
        }
    }

    // 좋아요를 누른 경우 Expressions.TRUE 리턴, memberId가 없을 경우 Expressions.FALSE 리턴
    private BooleanExpression isLiked(Long memberId) {
        if (memberId == null) {
            return Expressions.FALSE;
        }
        return JPAExpressions
                .selectOne()
                .from(like)
                .where(comment.id.eq(like.comment.id)
                        .and(like.member.id.eq(memberId)))
                .exists();
    }
}
