package com.campick.server.api.category.dto;

import com.campick.server.api.type.entity.VehicleTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TypeListResponseDto {
    List<String> types;
}
