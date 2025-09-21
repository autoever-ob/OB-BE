package com.campick.server.api.product.dto;

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
    private List<String> types;
    private List<String> options;
}
