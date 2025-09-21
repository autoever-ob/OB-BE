package com.campick.server.api.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class SellerResDto {
    private String nickName;
    private String role;
    private Double rating;
    private Integer sellingCount;
    private Integer completeCount;
    private Long userId;
}
