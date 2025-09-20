package com.campick.server.api.member.dto;

import com.campick.server.api.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReviewSummaryDto {
    private Long reviewId;
    private Long authorId;
    private String nickname;
    private String profileImage;
    private String content;
    private Double rating;
    private LocalDateTime createdAt;

    public static ReviewSummaryDto from(Review review) {
        return ReviewSummaryDto.builder()
                .reviewId(review.getId())
                .authorId(review.getAuthor().getId())
                .nickname(review.getAuthor().getNickname())
                .profileImage(review.getAuthor().getProfileImageUrl())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}