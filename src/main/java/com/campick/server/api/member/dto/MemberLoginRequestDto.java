package com.campick.server.api.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginRequestDto {

    @NotBlank
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    public String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    public String password;
}
