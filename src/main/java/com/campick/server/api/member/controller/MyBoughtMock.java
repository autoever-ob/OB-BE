package com.campick.server.api.member.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MyBoughtMock {

    public static class User {
        public String nickName;
        public String role;
        public Double rating;
        public int sellingCount;
        public int completeCount;
        public Long userId;

        public User(String nickName, String role, Double rating, int sellingCount, int completeCount, Long userId) {
            this.nickName = nickName;
            this.role = role;
            this.rating = rating;
            this.sellingCount = sellingCount;
            this.completeCount = completeCount;
            this.userId = userId;
        }
    }

    public static class Product {
        public String title;
        public String price;
        public LocalDateTime generation;
        public String kirometer;  // 주행거리
        public String location;
        public LocalDateTime createdAt;
        public String thumbNail;
        public String productId;
        public String status; // AVAILABLE, RESERVED, SOLD
        public Boolean isLiked;
        public User user;

        public Product(String title, String price, LocalDateTime generation, String kirometer,
                       String location, LocalDateTime createdAt, String thumbNail,
                       String productId, String status, Boolean isLiked, User user) {
            this.title = title;
            this.price = price;
            this.generation = generation;
            this.kirometer = kirometer;
            this.location = location;
            this.createdAt = createdAt;
            this.thumbNail = thumbNail;
            this.productId = productId;
            this.status = status;
            this.isLiked = isLiked;
            this.user = user;
        }
    }

    public static Page<Product> getPurchasedProducts() {
        List<Product> products = new ArrayList<>();
        User seller1 = new User("홍길동", "SELLER", 4.5, 20, 18, 101L);
        User seller2 = new User("김철수", "SELLER", 4.8, 15, 15, 102L);
        User seller3 = new User("이영희", "SELLER", 4.2, 10, 9, 103L);

        products.add(new Product("현대 아반떼 2021 1.6 가솔린", "1,150만 원", LocalDateTime.of(2021, 5, 10, 0, 0),
                "42,000 km", "서울 강남구", LocalDateTime.now(), "https://example.com/images/car1.jpg",
                "1", "SOLD", true, seller1));

        products.add(new Product("기아 K5 2019 2.0 가솔린", "1,400만 원", LocalDateTime.of(2019, 8, 15, 0, 0),
                "58,000 km", "서울 송파구", LocalDateTime.now(), "https://example.com/images/car2.jpg",
                "2", "SOLD", false, seller2));

        products.add(new Product("BMW 320d 2020", "3,200만 원", LocalDateTime.of(2020, 3, 20, 0, 0),
                "30,000 km", "경기 성남시", LocalDateTime.now(), "https://example.com/images/car3.jpg",
                "3", "SOLD", true, seller3));

        // 필요한 만큼 30개까지 반복해서 추가 가능
        return new PageImpl<>(products, PageRequest.of(0, 30), products.size());
    }
}
