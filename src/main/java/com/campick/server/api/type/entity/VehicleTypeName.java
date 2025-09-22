package com.campick.server.api.type.entity;

import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.response.ErrorStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum VehicleTypeName {
    MOTOR_HOME("모터홈"),
    TRAILER("트레일러"),
    CARAVAN("카라반"),
    TRUCK_CAMPER("트럭캠퍼"),
    ETC("기타");

    private final String korean;
    VehicleTypeName(String korean) {
        this.korean = korean;
    }

    @JsonCreator
    public static VehicleTypeName fromKorean(String koreanName) {
        for (VehicleTypeName type : values()) {
            if (type.getKorean().equals(koreanName)) {
                return type;
            }
        }
        throw new BadRequestException(ErrorStatus.INVALID_VEHICLE_TYPE.getMessage() + koreanName);
    }
}
