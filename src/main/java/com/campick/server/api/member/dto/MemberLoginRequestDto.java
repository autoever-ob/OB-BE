package com.campick.server.api.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginRequestDto {

    @NotBlank
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    public String email;

    @NotBlank
        // 추후 추가
//    @Pattern(
//            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
//            message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함해 8자 이상이어야 합니다."
//    )
    public String password;
}
