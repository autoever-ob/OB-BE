package com.campick.server.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginRequestDto {

    @NotBlank
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Schema(example = "user1@example.com")
    public String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, message = "비밀번호는 6자리 이상이어야 합니다.")
    @Schema(example = "123456")
    public String password;
}
