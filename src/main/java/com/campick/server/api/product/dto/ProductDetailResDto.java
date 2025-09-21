package com.campick.server.api.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
public class ProductDetailResDto {
    @JsonProperty("title")
    private String title;
    @JsonProperty("price")
    private String price;
    @JsonProperty("generation")
    private Integer generation;
    @JsonProperty("fuelType")
    private String fuelType;
    @JsonProperty("transmission")
    private String transmission;
    @JsonProperty("mileage")
    private String mileage;
    @JsonProperty("vehicleType")
    private String vehicleType;
    @JsonProperty("vehicleModel")
    private String vehicleModel;
    @JsonProperty("location")
    private String location;
    @JsonProperty("option")
    private List<OptionResDto> option;
    @JsonProperty("user")
    private SellerResDto user;
    @JsonProperty("plateHash")
    private String plateHash;
    @JsonProperty("description")
    private String description;
    @JsonProperty("productImageUrl")
    private List<String> productImageUrl;
    @JsonProperty("productId")
    private Long productId;
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    @JsonProperty("status")
    private String status;
    @JsonProperty("isLiked")
    private Boolean isLiked;
    @JsonProperty("likeCount")
    private Integer likeCount;
}
