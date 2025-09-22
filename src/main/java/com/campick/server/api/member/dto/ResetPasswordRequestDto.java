package com.campick.server.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResetPasswordRequestDto {
    @NotBlank(message = "인증 코드를 입력해주세요.")
    private String code;

//    @NotBlank(message = "비밀번호를 입력해주세요.")
//    @Size(min = 6, message = "비밀번호는 6자리 이상이어야 합니다.")
//    @Schema(example = "123456")
//    private String newPassword;
}
