package com.campick.server.api.car.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CarResponseDTO {
    private Long carId;
    private String carName;
}
