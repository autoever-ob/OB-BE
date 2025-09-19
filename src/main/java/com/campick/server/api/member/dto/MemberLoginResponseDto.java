package com.campick.server.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberLoginResponseDto {

    private String accessToken;
    private String refreshToken;



}