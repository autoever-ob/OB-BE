package com.campick.server.api.member.controller;

import com.campick.server.api.member.dto.EmailVerificationCodeRequestDto;
import com.campick.server.api.member.dto.EmailVerificationRequestDto;
import com.campick.server.api.member.dto.MemberLoginRequestDto;
import com.campick.server.api.member.dto.MemberLoginResponseDto;
import com.campick.server.api.member.dto.MemberSignUpRequestDto;
import com.campick.server.api.member.service.EmailService;
import com.campick.server.api.member.service.MemberService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequestMapping("/api/member")
@Tag(name = "auth", description = "인증 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final MemberService memberService;
    private final EmailService emailService;

    @Operation(
            summary = "이메일 회원가입 API", description = "회원정보를 받아 사용자를 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공")
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody MemberSignUpRequestDto requestDto) {
        memberService.signUp(requestDto);
        return ApiResponse.success_only(SuccessStatus.SEND_REGISTER_SUCCESS);
    }

    @Operation(
            summary = "이메일 로그인 API", description = "이메일과 비밀번호로 로그인 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "로그인 성공")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberLoginResponseDto>> login(@RequestBody MemberLoginRequestDto requestDto) {
        MemberLoginResponseDto loginResponseDto = memberService.login(requestDto);
        return ApiResponse.success(SuccessStatus.SEND_LOGIN_SUCCESS, loginResponseDto);
    }

    @Operation(
            summary = "이메일 인증코드 발송 API",
            description = "이메일 인증 코드를 발송합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "email : 사용자 이메일"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 인증코드 발송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "올바른 이메일 형식이 아닙니다."),
    })
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<Void>> getEmailVerification(@Valid @RequestBody EmailVerificationRequestDto emailVerificationRequestDto) {
        LocalDateTime requestedAt = LocalDateTime.now();
        String email = emailVerificationRequestDto.getEmail();

        emailService.sendVerificationEmail(email, requestedAt);
        return ApiResponse.success_only(SuccessStatus.SEND_EMAIL_VERIFICATION_CODE_SUCCESS);
    }


    @Operation(
            summary = "이메일 코드 인증 API",
            description = "발송된 이메일 인증 코드를 검증합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "code : 이메일로 발송된 인증코드"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 코드 인증 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "이메일 인증코드가 올바르지 않습니다."),
    })
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> verificationByCode(@RequestBody EmailVerificationCodeRequestDto emailVerificationCodeRequestDto) {
        LocalDateTime requestedAt = LocalDateTime.now();
        emailService.verifyEmail(emailVerificationCodeRequestDto.getCode(), requestedAt);
        return ApiResponse.success_only(SuccessStatus.SEND_EMAIL_VERIFICATION_SUCCESS);
    }

    @Operation(
            summary = "로그아웃 API", description = "로그아웃")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String accessToken) {
        memberService.logout(accessToken);
        return ApiResponse.success_only(SuccessStatus.LOGOUT_SUCCESS);
    }

    @Operation(
            summary = "access토큰 재발급 API", description = "만료된 access토큰으로 재발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "access토큰 재발급 성공")
    })
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<MemberLoginResponseDto>> reissue(@RequestHeader("Authorization") String accessToken) {
        MemberLoginResponseDto memberLoginResponseDto = memberService.reissueToken(accessToken);
        return ApiResponse.success(SuccessStatus.REISSUE_SUCCESS, memberLoginResponseDto);
    }

    @Operation(summary = "이메일 중복 검사", description = "입력한 이메일이 중복되는지 확인합니다.")
    @GetMapping("/check/email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = memberService.isEmailDuplicate(email);
        return ApiResponse.success(SuccessStatus.CHECK_EMAIL_DUPLICATE, isDuplicate);
    }

    @Operation(summary = "닉네임 중복 검사", description = "입력한 닉네임이 중복되는지 확인합니다.")
    @GetMapping("/check/nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkNicknameDuplicate(@RequestParam String nickname) {
        boolean isDuplicate = memberService.isNicknameDuplicate(nickname);
        return ApiResponse.success(SuccessStatus.CHECK_NICKNAME_DUPLICATE, isDuplicate);
    }
}
