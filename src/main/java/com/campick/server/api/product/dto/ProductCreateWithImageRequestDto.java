package com.campick.server.api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProductCreateWithImageRequestDto {
    private String mileage;          // 주행거리
    private String vehicleType;      // 차종
    private String vehicleModel;     // 자동차 브랜드
    private String price;            // 판매가격
    private String location;         // 판매지역
    private String plateHash;        // 차량번호
    private String title;            // 제목
    private String description;      // 설명

    private List<ProductCreateRequestDto.OptionDTO> option;       // 옵션 리스트

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionDTO {
        private String optionName;
        private Boolean isInclude;
    }
}

