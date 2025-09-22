package com.campick.server.api.engine.entity;

import lombok.Getter;

@Getter
public enum FuelType {
    GASOLINE("가솔린"),
    DIESEL("디젤"),
    ELECTRIC("전기"),
    HYBRID("하이브리드");

    private final String korean;
    FuelType(String korean) {
        this.korean = korean;
    }
}
