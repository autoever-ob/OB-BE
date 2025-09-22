package com.campick.server.api.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PasswordResetRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
