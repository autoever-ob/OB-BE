package com.campick.server.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberLoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private Long memberId;
    private String nickname;
    private String phoneNumber;
    private Long dealerId;
    private String role;

}
