package com.campick.server.api.member.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MySoldMock {

    public static Page<ProductDto> getMyProducts() {
        UserDto seller = new UserDto("홍길동", "SELLER", 4.7, 15, 12, 1001L);

        List<ProductDto> products = new ArrayList<>();

        for (long i = 1; i <= 30; i++) {
            products.add(new ProductDto(
                    "차량 제목 " + i,
                    (1000 + i * 10) + "만 원",
                    LocalDateTime.of(2020 + (int)(i % 3), (int)(i % 12) + 1, (int)(i % 28) + 1, 0, 0),
                    (10_000 + i * 1000) + " km",
                    "서울 구" + i + "동",
                    LocalDateTime.now().minusDays(i),
                    "https://example.com/images/car" + i + ".jpg",
                    i,
                    i % 3 == 0 ? "SOLD" : (i % 3 == 1 ? "AVAILABLE" : "RESERVED"),
                    i % 2 == 0,
                    seller
            ));
        }

        return new PageImpl<>(products, PageRequest.of(0, 30), products.size());
    }

    public static class ProductDto {
        public String title;
        public String price;
        public LocalDateTime generation;
        public String mileage;
        public String location;
        public LocalDateTime createdAt;
        public String thumbNail;
        public Long productId;
        public String status;
        public Boolean isLiked;
        public UserDto user;

        public ProductDto(String title, String price, LocalDateTime generation, String mileage, String location,
                          LocalDateTime createdAt, String thumbNail, Long productId, String status, Boolean isLiked, UserDto user) {
            this.title = title;
            this.price = price;
            this.generation = generation;
            this.mileage = mileage;
            this.location = location;
            this.createdAt = createdAt;
            this.thumbNail = thumbNail;
            this.productId = productId;
            this.status = status;
            this.isLiked = isLiked;
            this.user = user;
        }
    }

    public static class UserDto {
        public String nickName;
        public String role;
        public Double rating;
        public int sellingCount;
        public int completeCount;
        public Long userId;

        public UserDto(String nickName, String role, Double rating, int sellingCount, int completeCount, Long userId) {
            this.nickName = nickName;
            this.role = role;
            this.rating = rating;
            this.sellingCount = sellingCount;
            this.completeCount = completeCount;
            this.userId = userId;
        }
    }
}
