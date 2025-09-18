package com.campick.server.api.member.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MySellMock {
    public static class SellProductDto {
        public String title;
        public String price;
        public LocalDateTime generation;
        public String kilometer;  // 주행거리
        public String location;
        public LocalDateTime createdAt;
        public String thumbNail;
        public String productId;
        public String status; // AVAILABLE, RESERVED, SOLD
        public Boolean isLiked;

        public SellProductDto(String title, String price, LocalDateTime generation, String kilometer,
                       String location, LocalDateTime createdAt, String thumbNail,
                       String productId, String status, Boolean isLiked) {
            this.title = title;
            this.price = price;
            this.generation = generation;
            this.kilometer = kilometer;
            this.location = location;
            this.createdAt = createdAt;
            this.thumbNail = thumbNail;
            this.productId = productId;
            this.status = status;
            this.isLiked = isLiked;
        }
    }

    public static Page<SellProductDto> getMyProducts() {
        List<SellProductDto> products = new ArrayList<>();
        products.add(new SellProductDto("현대 아반떼 2021 1.6 가솔린", "1,150만 원", LocalDateTime.of(2021, 5, 10, 0, 0),
                "42,000 km", "서울 강남구", LocalDateTime.now(), "https://example.com/images/car1.jpg",
                "1", "AVAILABLE", true));
        products.add(new SellProductDto("기아 K5 2019 2.0 가솔린", "1,400만 원", LocalDateTime.of(2019, 8, 15, 0, 0),
                "58,000 km", "서울 송파구", LocalDateTime.now(), "https://example.com/images/car2.jpg",
                "2", "RESERVED", false));
        products.add(new SellProductDto("BMW 320d 2020", "3,200만 원", LocalDateTime.of(2020, 3, 20, 0, 0),
                "30,000 km", "경기 성남시", LocalDateTime.now(), "https://example.com/images/car3.jpg",
                "3", "SOLD", true));
        // 필요한 만큼 30개까지 반복해서 추가 가능
        return new PageImpl<>(products, PageRequest.of(0, 30), products.size());
    }
}
