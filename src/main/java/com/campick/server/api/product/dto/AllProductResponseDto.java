package com.campick.server.api.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllProductResponseDto {
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
    private String productId;
    @JsonProperty("status")
    private String status;
    //private Boolean isLiked;
    //private Long likeCount; 로그인 되고 나면 추가

}
