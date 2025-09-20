package com.campick.server.api.member.dto;

import com.campick.server.api.member.entity.Member;
import com.campick.server.api.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    private Long id;
    private String nickname;
    private Double rating;
    private List<ReviewSummaryDto> reviews;
    private LocalDateTime createdAt;
    private String profileImage;
    private String description;

    public static MemberResponseDto of(Member member, List<Review> reviews) {
        Double dealerRating = null;
        if (member.getDealer() != null) {
            dealerRating = member.getDealer().getRating();
        }
        return MemberResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImageUrl())
                .description(member.getDescription())
                .createdAt(member.getCreatedAt())
                .rating(dealerRating)
                .reviews(reviews.stream()
                        .map(ReviewSummaryDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
