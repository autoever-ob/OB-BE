package com.campick.server.api.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResDto {
    @JsonProperty("title")
    private String title;
    @JsonProperty("price")
    private String price;
    @JsonProperty("mileage")
    private String mileage;
    @JsonProperty("location")
    private String location;
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    @JsonProperty("thumbNail")
    private String thumbNail;
    @JsonProperty("productId")
    private Long productId;
    @JsonProperty("status")
    private String status;
    private Boolean isLiked;
    private Integer likeCount; //로그인 되고 나면 추가

    public ProductResDto(String title, String price, String mileage, String location, LocalDateTime createdAt, String thumbnailUrl, Long productId, String status) {
        this.title = title;
        this.price = price;
        this.mileage = mileage;
        this.location = location;
        this.createdAt = createdAt;
        this.thumbNail = thumbnailUrl;
        this.productId = productId;
        this.status = status;
    }
}
