package com.campick.server.api.member.controller;

import com.campick.server.api.member.dto.*;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.service.EmailService;
import com.campick.server.api.member.service.MemberService;
import com.campick.server.common.config.security.SecurityMember;
import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.ErrorStatus;
import com.campick.server.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;


@RequestMapping("/api/member")
@Tag(name="member", description = "멤버 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
public class MemberController {

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
    public ResponseEntity<ApiResponse<MemberLoginResponseDto>> login(@RequestBody MemberLoginRequestDto requestDto, HttpServletResponse response) {
        MemberLoginResponseDto loginResponseDto = memberService.login(requestDto);

        Cookie cookie = new Cookie("refresh", loginResponseDto.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);

        return ApiResponse.success(SuccessStatus.SEND_LOGIN_SUCCESS,loginResponseDto);
    }

    @Operation(
            summary = "이메일 인증코드 발송 API",
            description = "이메일 인증 코드를 발송합니다.<br>"
            +"<p>"
            +"호출 필드 정보) <br>"
            +"email : 사용자 이메일"
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
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }
        memberService.logout(refresh);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ApiResponse.success_only(SuccessStatus.LOGOUT_SUCCESS);
    }

    @Operation(summary = "비밀번호 변경 API", description = "사용자 정보를 가지고 비밀번호를 변경합니다.")
    @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공"))
    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@Valid @RequestBody PasswordUpdateRequestDto requestDto,
                                                            @AuthenticationPrincipal SecurityMember securityMember){
    memberService.updatePassword(
            securityMember.getEmail(),
            requestDto
    );
    return ApiResponse.success_only(SuccessStatus.UPDATE_PASSWORD_SUCCESS);
    }

    // 마이페이지 프로필 이미지 변경
    @Operation(summary = "프로필 이미지 변경 API", description = "사용자의 프로필 이미지를 변경합니다.")
    @PatchMapping(value = "/image", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<String>> updateProfileImage(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        String imageUrl = memberService.updateProfileImage(securityMember.getEmail(), file);
        return ApiResponse.success(SuccessStatus.UPDATE_PROFILE_IMAGE_SUCCESS, imageUrl);
    }

    // 회원 정보 조회
    @Operation(summary = "회원정보 조회 API", description = "회원 정보를 조회합니다.")
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<MemberResponseDto>> getMemberInfo(@AuthenticationPrincipal SecurityMember securityMember) {
        MemberResponseDto memberResponseDto = memberService.getMemberById(securityMember.getId());
        return ApiResponse.success(SuccessStatus.SEND_LOGIN_SUCCESS, memberResponseDto);
    }

    // 특정 회원 정보 조회
    @Operation(summary = "특정 회원 정보 조회", description = "ID를 이용해 회원 정보를 조회합니다.")
    @GetMapping("info/{id}")
    public ResponseEntity<ApiResponse<MemberResponseDto>> getMemberById(@PathVariable Long id) {
        MemberResponseDto dto = memberService.getMemberById(id);
        return ApiResponse.success(SuccessStatus.SEND_FOLLOWING_LIST_SUCCESS, dto);
    }

    @Operation(
            summary = "access토큰 재발급 API", description = "access토큰 재발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "access토큰 재발급 성공")
    })
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<MemberLoginResponseDto>> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }
        MemberLoginResponseDto dto = memberService.reissueToken(refresh);

        Cookie newRefreshCookie = new Cookie("refresh", dto.getRefreshToken());
        newRefreshCookie.setHttpOnly(true);
        newRefreshCookie.setPath("/");
        newRefreshCookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(newRefreshCookie);

        return ApiResponse.success(SuccessStatus.REISSUE_SUCCESS, dto);
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
