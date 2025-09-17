package com.campick.server.api.member.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class UserMock {

    public static UserResponseDto getUserInfo() {
        return new UserResponseDto(
                "홍길동",
                4.7,
                "SELLER",
                Arrays.asList(
                        new ReviewDto("김철수", "https://example.com/profiles/user1.jpg", 5.0, "친절하고 차량 상태 설명도 명확했습니다.", LocalDateTime.now().minusDays(3)),
                        new ReviewDto("박영희", "https://example.com/profiles/user2.jpg", 4.5, "거래가 원활했어요. 추천합니다.", LocalDateTime.now().minusDays(10)),
                        new ReviewDto("이민수", "https://example.com/profiles/user3.jpg", 4.8, "차량 상태가 설명과 같았습니다.", LocalDateTime.now().minusDays(20))
                ),
                LocalDateTime.now().minusYears(2),
                "https://example.com/profiles/main.jpg",
                "안녕하세요! 차량 거래를 전문으로 하는 중고차 판매자입니다. 항상 정직하게 거래합니다."
        );
    }

    public static class UserResponseDto {
        public String nickName;
        public Double ratingAverage;
        public String role;
        public List<ReviewDto> review;
        public LocalDateTime createdAt;
        public String profileImage;
        public String description;

        public UserResponseDto(String nickName, Double ratingAverage, String role, List<ReviewDto> review,
                               LocalDateTime createdAt, String profileImage, String description) {
            this.nickName = nickName;
            this.ratingAverage = ratingAverage;
            this.role = role;
            this.review = review;
            this.createdAt = createdAt;
            this.profileImage = profileImage;
            this.description = description;
        }
    }

    public static class ReviewDto {
        public String nickName;
        public String profileImage;
        public Double rating;
        public String content;
        public LocalDateTime createdAt;

        public ReviewDto(String nickName, String profileImage, Double rating, String content, LocalDateTime createdAt) {
            this.nickName = nickName;
            this.profileImage = profileImage;
            this.rating = rating;
            this.content = content;
            this.createdAt = createdAt;
        }
    }
}
