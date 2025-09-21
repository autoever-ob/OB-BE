package com.campick.server.api.member.controller;

import com.campick.server.api.member.dto.ResetPasswordRequestDto;
import com.campick.server.api.member.dto.SendResetRequestDto;
import com.campick.server.api.member.service.PasswordResetService;
import com.campick.server.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.campick.server.common.response.SuccessStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password-reset")
@Tag(name="Password-Reset", description = "Password-Reset 관련 API 입니다.")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    // 1. 비밀번호 재설정 링크 이메일 전송
    @Operation(summary = "비밀번호 재설정 코드 이메일 전송", description = "비밀번호 재설정 코드 이메일 전송")
    @PostMapping("/send-link")
    public ResponseEntity<ApiResponse<Void>> sendResetLink(@RequestBody SendResetRequestDto request) {
        passwordResetService.sendResetLink(request.getEmail());
        return ApiResponse.success_only(SuccessStatus.PASSWORD_RESET_LINK_SENT);
    }

    // 2. 재설정 실행 (code + new password)
    @Operation(summary = "비밀번호 재설정", description = "비밀번호 재설정")
    @PutMapping
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequestDto request) {
        passwordResetService.resetPasswordWithCode(request.getCode());
        return ApiResponse.success_only(SuccessStatus.PASSWORD_RESET_SUCCESS);
    }
}