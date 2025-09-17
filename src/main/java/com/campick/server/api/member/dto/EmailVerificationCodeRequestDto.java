package com.campick.server.api.member.dto;

import lombok.Getter;

@Getter
public class EmailVerificationCodeRequestDto {
    private String code;
}
