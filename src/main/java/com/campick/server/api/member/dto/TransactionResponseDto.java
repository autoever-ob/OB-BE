package com.campick.server.api.member.dto;

import com.campick.server.api.product.entity.ProductImage;
import com.campick.server.api.product.entity.ProductStatus;
import com.campick.server.api.transaction.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class TransactionResponseDto  {
    private Long memberId;
    private Long productId;
    private String title;
    private Integer cost;
    private Integer generation;
    private Integer mileage;
    private String location;
    private String productImageUrl;
//    private String fuelType;
//    private String transmission;
    private ProductStatus status;
    private LocalDateTime createdAt;

    public static TransactionResponseDto from(Transaction transaction, String soldOrbought) {

        // 판 매물과 산 매물 동시에 하기 위한 작업
        Long memberId = soldOrbought.equals("SOLD") ? transaction.getBuyer().getId() : transaction.getSeller().getId();

        String productImageUrl = transaction.getProduct().getImages().stream()
                .filter(image -> Boolean.TRUE.equals(image.getIsThumbnail()))
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);

        return TransactionResponseDto.builder()
                .memberId(memberId)
                .productId(transaction.getProduct().getId())
                .title(transaction.getProduct().getTitle())
                .cost(transaction.getProduct().getCost())
                .generation(transaction.getProduct().getGeneration())
                .mileage(transaction.getProduct().getMileage())
                .location(transaction.getProduct().getLocation())
                .productImageUrl(productImageUrl)
//                .fuelType(transaction.getProduct().getCar().getEngine().getFuelType().getKorean())
//                .transmission(transaction.getProduct().getCar().getEngine().getTransmission().getKorean())
                .status(transaction.getProduct().getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
