package com.mytypeworldcup.mytypeworldcup.domain.comment.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class CommentResponseDto {
    // 댓글 정보
    private Long id;
    private String content;
    private String candidateName;
    private Integer likesCount;
    private Boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    // 작성자 정보
    private Long memberId;
    private String nickname;

    // 월드컵 정보
    private Long worldCupId;

    @Builder
    @QueryProjection
    public CommentResponseDto(Long id,
                              String content,
                              String candidateName,
                              Integer likesCount,
                              Boolean isLiked,
                              LocalDateTime createdAt,
                              LocalDateTime modifiedAt,
                              Long memberId,
                              String nickname,
                              Long worldCupId) {
        this.id = id;
        this.content = content;
        this.candidateName = candidateName;
        this.likesCount = likesCount;
        this.isLiked = isLiked;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.memberId = memberId;
        this.nickname = nickname;
        this.worldCupId = worldCupId;
    }
}
