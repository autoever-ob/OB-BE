package com.campick.server.api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageUploadResponseDto {
    private String productImageUrl;

    public static ProductImageUploadResponseDto from(Map<String, String> urls) {
        return ProductImageUploadResponseDto.builder()
                .productImageUrl(urls.get("productImageUrl"))
                .build();
    }
}
