package com.campick.server.api.member.dto;

import com.campick.server.api.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private String nickName;
    private String content;
    private Double rating;
    private String profileImage;

    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .nickName(review.getAuthor().getNickname())
                .content(review.getContent())
                .rating(review.getRating())
                .profileImage(review.getAuthor().getProfileImageUrl())
                .build();
    }
}
