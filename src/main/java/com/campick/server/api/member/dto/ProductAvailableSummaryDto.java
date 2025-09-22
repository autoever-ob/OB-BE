package com.campick.server.api.member.dto;

import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.entity.ProductImage;
import com.campick.server.api.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

// Product 엔티티에 연관된 멤버, 차, 엔진, 좋아요를 전부 합쳐서 보내준다.
@Getter
@AllArgsConstructor
@Builder
public class ProductAvailableSummaryDto {
    private Long productId;
    private String title;
    private Integer cost;
    private Integer generation;
    private Integer mileage;
    private String location;
    private LocalDateTime createdAt;
    private String productImageUrl;
//    private String fuelType;
//    private String transmission;
    private ProductStatus status;

    public static ProductAvailableSummaryDto from(Product product){
        String productImageUrl = product.getImages().stream()
                .filter(image -> Boolean.TRUE.equals(image.getIsThumbnail()))
                .findFirst()
                .map(ProductImage::getThumbnailUrl)
                .orElse(null);

        return ProductAvailableSummaryDto.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .cost(product.getCost())
                .generation(product.getGeneration())
                .mileage(product.getMileage())
                .location(product.getLocation())
                .createdAt(product.getCreatedAt())
                .productImageUrl(productImageUrl)
//                .fuelType(product.getCar().getEngine().getFuelType().getKorean())
//                .transmission(product.getCar().getEngine().getTransmission().getKorean())
                .status(product.getStatus())
                .build();
    }
}
