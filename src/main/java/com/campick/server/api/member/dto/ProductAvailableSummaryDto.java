package com.campick.server.api.member.dto;

import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.entity.ProductImage;
import com.campick.server.api.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<ProductImage> thumbnailUrl;
    private ProductStatus status;

    public static ProductAvailableSummaryDto from(Product product){
        // Builder 어노테이션을 활용해 null 값이여도 생성하게 끔
        //! TODO : 종류, 모델 , 연식, 엔진 타입별로 검색해야되기 때문에
        //! 수정되어야함
        return ProductAvailableSummaryDto.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .cost(product.getCost())
                .generation(product.getGeneration())
                .mileage(product.getMileage())
                .location(product.getLocation())
                .createdAt(product.getCreatedAt())
                .thumbnailUrl(product.getImages())
                .status(product.getStatus())
                .build();
    }
}
