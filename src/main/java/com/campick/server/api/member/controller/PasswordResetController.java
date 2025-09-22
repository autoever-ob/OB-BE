package com.campick.server.api.member.controller;

import com.campick.server.api.member.dto.PasswordResetRequestDto;
import com.campick.server.api.member.dto.PasswordUpdateRequestDto;
import com.campick.server.api.member.dto.ResetPasswordRequestDto;
import com.campick.server.api.member.dto.SendResetRequestDto;
import com.campick.server.api.member.service.PasswordResetService;
import com.campick.server.common.config.security.SecurityMember;
import com.campick.server.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.campick.server.common.response.SuccessStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password-reset")
@Tag(name="Password-Reset", description = "로그인이 안된 상태에서 비밀번호를 변경할 수 있는 컨트롤러입니다.")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    // 1. 비밀번호 재설정 링크 이메일 전송
    @Operation(summary = "비밀번호 재설정 코드 이메일 전송", description = "비밀번호 재설정 코드 이메일 전송")
    @PostMapping("/send-link")
    public ResponseEntity<ApiResponse<Void>> sendResetLink(@RequestBody SendResetRequestDto request) {
        passwordResetService.sendResetLink(request.getEmail());
        return ApiResponse.success_only(SuccessStatus.PASSWORD_RESET_LINK_SENT);
    }

    // 2. 재설정 실행 (code)
    @Operation(summary = "변경 코드 검증", description = "이메일로 보낸 코드를 검증합니다")
    @PatchMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequestDto request) {
        passwordResetService.verifyCode(request.getCode());
        return ApiResponse.success_only(SuccessStatus.PASSWORD_RESET_CODE_VERIFIED);
    }

    // 3. 비밀번호 변경
    @Operation(summary = "비밀번호 변경 API", description = "사용자 정보를 가지고 비밀번호를 변경합니다.")
    @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"))
    @PatchMapping()
    public ResponseEntity<ApiResponse<Void>> updatePassword(@Valid @RequestBody PasswordResetRequestDto requestDto
                                                            ) {
        passwordResetService.resetPassword(requestDto.getEmail(), requestDto.getPassword());
        return ApiResponse.success_only(SuccessStatus.UPDATE_PASSWORD_SUCCESS);
    }
}