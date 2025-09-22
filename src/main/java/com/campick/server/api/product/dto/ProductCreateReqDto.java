package com.campick.server.api.product.dto;

import com.campick.server.api.type.entity.VehicleTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class ProductCreateReqDto {
    private String mileage;          // 주행거리
    @Schema(example = "모터홈")
    private VehicleTypeName vehicleType;      // 차종
    private String vehicleModel;     // 자동차 브랜드
    private Integer generation;      // 연식
    private String price;            // 판매가격
    private String location;         // 판매지역
    private String plateHash;        // 차량번호
    private String title;            // 제목
    private String description;      // 설명
    private String mainProductImageUrl; // 메인 이미지

    private List<String> productImageUrl; // 차량 이미지 URL 목록
    private List<OptionDto> option;       // 옵션 리스트
}

