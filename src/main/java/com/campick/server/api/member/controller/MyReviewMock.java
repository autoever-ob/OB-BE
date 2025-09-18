package com.campick.server.api.member.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MyReviewMock {

    public static class Review {
        public String nickName;
        public String content;
        public Double rating;
        public String profileImage;

        public Review(String nickName, String content, Double rating, String profileImage) {
            this.nickName = nickName;
            this.content = content;
            this.rating = rating;
            this.profileImage = profileImage;
        }
    }

    public static Page<Review> getReviews() {
        List<Review> reviews = new ArrayList<>();

        reviews.add(new Review("홍길동", "차 상태가 정말 좋습니다. 만족스러워요!", 4.5, "https://example.com/images/profile1.jpg"));
        reviews.add(new Review("김철수", "판매자 친절하고 차량 상태가 깔끔했어요.", 4.8, "https://example.com/images/profile2.jpg"));
        reviews.add(new Review("이영희", "가격 대비 만족스럽습니다. 추천합니다.", 4.2, "https://example.com/images/profile3.jpg"));
        reviews.add(new Review("박민수", "조금 흠집이 있었지만 전체적으로 만족.", 3.9, "https://example.com/images/profile4.jpg"));
        reviews.add(new Review("최수진", "거래가 신속하게 진행되었습니다.", 5.0, "https://example.com/images/profile5.jpg"));

        return new PageImpl<>(reviews, PageRequest.of(0, 30), reviews.size());
    }
}
