package com.campick.server.api.product.dto;

import com.campick.server.api.type.entity.VehicleTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class FilterReqDto {
    private Integer mileageFrom;
    private Integer mileageTo;
    private Integer costFrom;
    private Integer costTo;
    private Integer generationFrom;
    private Integer generationTo;
    @JsonProperty("types")
    private List<String> types;
    private List<String> options;
}
