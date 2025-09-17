package com.campick.server.api.member.dto;

import com.campick.server.api.favorite.entity.Favorite;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.entity.ProductImage;
import com.campick.server.api.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProductAvailableSummaryDto {
    private Long productId;
    private String title;
    private Integer cost;
    private Integer mileage;
    private String location;
    private LocalDateTime createdAt;
    private List<ProductImage> thumbnailUrl;
    private ProductStatus status;
    private List<Favorite> isLiked;

    public static ProductAvailableSummaryDto from(List<Product> product){
        return ProductAvailableSummaryDto.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .cost(product.getCost())
                .mileage(product.getMileage())
                .location(product.getLocation())
                .createdAt(product.getCreatedAt())
                .thumbnailUrl(product.getImages())
                .status(product.getStatus())
                .isLiked(product.getLikes())
                .build();
    }
}
