package com.campick.server.api.engine.entity;

import lombok.Getter;

@Getter
public enum Transmission {
    MANUAL("수동"), AUTOMATIC("자동");

    private final String korean;
    Transmission(String korean) {
        this.korean = korean;
    }
}
