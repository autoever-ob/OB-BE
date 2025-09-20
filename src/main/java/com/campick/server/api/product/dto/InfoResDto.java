package com.campick.server.api.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class InfoResDto {
    private List<String> type;
    private List<String> model;
    private List<String> option;
}
