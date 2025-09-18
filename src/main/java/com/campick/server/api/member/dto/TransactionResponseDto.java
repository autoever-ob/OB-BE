package com.campick.server.api.member.dto;

import com.campick.server.api.product.entity.ProductImage;
import com.campick.server.api.transaction.entity.Transaction;
import com.campick.server.api.transaction.entity.TransactionType;
import com.campick.server.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class TransactionResponseDto extends BaseTimeEntity {
    private Long transactionId;
    private Long memberId;
    private Long productId;
    private String title;
    private Integer cost;
    private Integer generation;
    private Integer mileage;
    private String location;
    private List<String> thumbnailUrls;
    private TransactionType transactionType;
    private LocalDateTime createdAt;

    public static TransactionResponseDto from(Transaction transaction) {

        // productId 기준으로 제품 사진 불러오기
        List<String> thumbnailUrl = transaction.getProduct().getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList();

        return TransactionResponseDto.builder()
                .transactionId(transaction.getId())
                .memberId(transaction.getBuyer().getId())
                .productId(transaction.getProduct().getId())
                .title(transaction.getProduct().getTitle())
                .cost(transaction.getProduct().getCost())
                .generation(transaction.getProduct().getGeneration())
                .mileage(transaction.getProduct().getMileage())
                .location(transaction.getProduct().getLocation())
                .thumbnailUrls(thumbnailUrl)
                .transactionType(transaction.getType())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
