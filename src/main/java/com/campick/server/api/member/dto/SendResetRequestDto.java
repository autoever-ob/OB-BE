package com.campick.server.api.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendResetRequestDto {
    @NotBlank
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}
