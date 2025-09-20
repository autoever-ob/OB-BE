package com.campick.server.api.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class RecommendResDto {
    private ProductResDto newVehicle;
    private ProductResDto hotVehicle;
}
