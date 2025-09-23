package com.campick.server.api.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ModelListResponseDto {
    List<String> models;
}
