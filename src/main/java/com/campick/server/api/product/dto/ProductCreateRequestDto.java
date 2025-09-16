package com.campick.server.api.product.dto;

import com.campick.server.api.car.entity.Car;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.product.entity.Product;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequestDto {
    private String mileage;          // 주행거리
    private String vehicleType;      // 차종
    private String vehicleModel;     // 자동차 브랜드
    private String price;            // 판매가격
    private String location;         // 판매지역
    private String plateHash;        // 차량번호
    private String title;            // 제목
    private String description;      // 설명
    private String mainProductImageUrl; // 메인 이미지

    private List<String> productImageUrl; // 차량 이미지 URL 목록
    private List<OptionDTO> option;       // 옵션 리스트

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionDTO {
        private String optionName;
        private Boolean isInclude;
    }
}
