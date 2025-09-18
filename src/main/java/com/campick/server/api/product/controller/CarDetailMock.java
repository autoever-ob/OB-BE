package com.campick.server.api.product.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class CarDetailMock {

    public static class User {
        public String nickName;
        public String role;
        public Double rating;
        public int sellingCount;
        public int completeCount;
        public long userId;

        public User(String nickName, String role, Double rating, int sellingCount, int completeCount, long userId) {
            this.nickName = nickName;
            this.role = role;
            this.rating = rating;
            this.sellingCount = sellingCount;
            this.completeCount = completeCount;
            this.userId = userId;
        }
    }

    public static class Option {
        public String optionName;
        public boolean isInclude;

        public Option(String optionName, boolean isInclude) {
            this.optionName = optionName;
            this.isInclude = isInclude;
        }
    }

    public static class CarDetailResponseDto {
        public LocalDate generation;
        public String mileage;
        public String vehicleType;
        public String vehicleModel;
        public String price;
        public String location;
        public User user;
        public String plateHash;
        public String description;
        public List<String> productImage;
        public List<Option> option;
        public boolean isLiked;
        public String status;
        public long productId;
        public int likeCount;

        public CarDetailResponseDto(LocalDate generation, String mileage, String vehicleType, String vehicleModel,
                                    String price, String location, User user, String plateHash, String description,
                                    List<String> productImage, List<Option> option, boolean isLiked, String status,
                                    long productId, int likeCount) {
            this.generation = generation;
            this.mileage = mileage;
            this.vehicleType = vehicleType;
            this.vehicleModel = vehicleModel;
            this.price = price;
            this.location = location;
            this.user = user;
            this.plateHash = plateHash;
            this.description = description;
            this.productImage = productImage;
            this.option = option;
            this.isLiked = isLiked;
            this.status = status;
            this.productId = productId;
            this.likeCount = likeCount;
        }
    }

    public static CarDetailResponseDto getCarDetail() {
        User seller = new User("홍길동", "SELLER", 4.7, 12, 30, 101L);

        List<String> images = Arrays.asList(
                "https://example.com/images/car1.jpg",
                "https://example.com/images/car2.jpg",
                "https://example.com/images/car3.jpg"
        );

        List<Option> options = Arrays.asList(
                new Option("썬루프", true),
                new Option("네비게이션", true),
                new Option("후방카메라", false),
                new Option("가죽시트", true)
        );

        return new CarDetailResponseDto(
                LocalDate.of(2021, 5, 1),
                "42,000 km",
                "세단",
                "현대 아반떼",
                "1,150만 원",
                "서울 강남구",
                seller,
                "12가3456",
                "차량 상태 좋고, 사고 이력 없음. 실내 깔끔하게 관리됨.",
                images,
                options,
                true,
                "AVAILABLE",
                1L,
                15
        );
    }
}
