package com.campick.server.api.member.controller;

import com.campick.server.api.member.dto.*;
import com.campick.server.api.member.service.EmailService;
import com.campick.server.api.member.service.MemberService;
import com.campick.server.api.member.service.PasswordResetService;
import com.campick.server.common.config.security.SecurityMember;
import com.campick.server.common.dto.PageResponseDto;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;


@RequestMapping("/api/member")
@Tag(name = "member", description = "멤버 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;

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

    @Operation(summary = "회원 탈퇴 API", description = "현재 로그인된 사용자를 탈퇴 처리합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteMember(@AuthenticationPrincipal SecurityMember securityMember) {
        memberService.deleteMember(securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.DELETE_MEMBER_SUCCESS);
    }

    @Operation(summary = "비밀번호 변경 API", description = "사용자 정보를 가지고 비밀번호를 변경합니다.")
    @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"))
    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@Valid @RequestBody PasswordUpdateRequestDto requestDto,
                                                            @AuthenticationPrincipal SecurityMember securityMember) {
        memberService.updatePassword(
                securityMember.getEmail(),
                requestDto
        );
        return ApiResponse.success_only(SuccessStatus.UPDATE_PASSWORD_SUCCESS);
    }

    @Operation(summary = "회원 정보 수정 API", description = "현재 로그인된 사용자의 정보를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "닉네임이 중복됩니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다.")
    })
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateMemberInfo(
            @AuthenticationPrincipal SecurityMember securityMember,
            @RequestBody MemberUpdateRequestDto requestDto) {
        memberService.updateMemberInfo(securityMember.getId(), requestDto);
        return ApiResponse.success_only(SuccessStatus.UPDATE_MEMBER_INFO_SUCCESS);
    }

    @Operation(summary = "프로필 이미지 변경 API", description = "사용자의 프로필 이미지를 변경합니다.")
    @PutMapping(value = "/image", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<ProfileImageUpdateResponseDto>> updateProfileImage(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        Map<String, String> imageUrls = memberService.updateProfileImage(securityMember.getEmail(), file);
        return ApiResponse.success(SuccessStatus.UPDATE_PROFILE_IMAGE_SUCCESS, ProfileImageUpdateResponseDto.from(imageUrls));
    }

    @Operation(summary = "특정 회원 정보 조회", description = "ID를 이용해 회원 정보를 조회합니다.")
    @GetMapping("info/{id}")
    public ResponseEntity<ApiResponse<MemberResponseDto>> getMemberById(@PathVariable Long id) {
        MemberResponseDto dto = memberService.getMemberById(id);
        return ApiResponse.success(SuccessStatus.SEND_FOLLOWING_LIST_SUCCESS, dto);
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

    @Operation(summary = "로그인된 사용자 비밀번호 확인", description = "로그인된 사용자의 비밀번호를 확인합니다")
    @PostMapping("/check/password")
    public ResponseEntity<ApiResponse<Boolean>> checkPasswordValidation(@AuthenticationPrincipal SecurityMember securityMember,
                                                                        @RequestBody MemberPasswordCheckRequestDto requestDto) {
        boolean isValidation = memberService.checkPasswordValidation(securityMember.getId(), requestDto.getPassword());
        return ApiResponse.success(SuccessStatus.CHECK_PASSWORD_VALIDATION, isValidation);
    }

    @Operation(summary = "{memberId}별 모든 매물 리스트", description = "{memberId}별 사용자의 모든 매물을 봅니다.")
    @GetMapping("/product/all/{memberId}")
    public ResponseEntity<ApiResponse<PageResponseDto<ProductAllSummaryDto>>> getMemberProductsAll(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<ProductAllSummaryDto> memberProductsIsAvailable = memberService.getMemberProductsAll(memberId, pageable);

        return ApiResponse.success(SuccessStatus.SEND_MEMBER_PRODUCTS_ALL_SUCCESS, memberProductsIsAvailable);
    }


    @Operation(summary = "{memberId}별 매물 중 팔거나 예약 중인 매물 리스트", description = "{memberId}별 사용자가 팔고 있는 매물을 봅니다.")
    @GetMapping("/product/sell-or-reserve/{memberId}")
    public ResponseEntity<ApiResponse<PageResponseDto<ProductAvailableSummaryDto>>> getMemberProducts(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<ProductAvailableSummaryDto> memberProductsIsAvailable = memberService.getMemberProducts(memberId, pageable);

        return ApiResponse.success(SuccessStatus.SEND_MEMBER_PRODUCTS_AVAILABLE_SUCCESS, memberProductsIsAvailable);
    }

    // 기록 부분
    @Operation(summary = "{memberId}별 판 매물 조회", description = "{memberId}별 판 매물 기록을 봅니다")
    @GetMapping("/product/sold/{memberId}")
    public ResponseEntity<ApiResponse<PageResponseDto<TransactionResponseDto>>> getMemberSold(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<TransactionResponseDto> memberProductsIsAvailable = memberService.getMemberSold(memberId, pageable);

        return ApiResponse.success(SuccessStatus.SEND_MEMBER_SOLD_PRODUCTS_SUCCESS, memberProductsIsAvailable);
    }


    @Operation(summary = "{memberId}별 산 매물 조회", description = "{memberId}별 산 매물 기록을 봅니다")
    @GetMapping("/product/bought/{memberId}")
    public ResponseEntity<ApiResponse<PageResponseDto<TransactionResponseDto>>> getMemberBought(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<TransactionResponseDto> memberProductsIsAvailable = memberService.getMemberBought(memberId, pageable);

        return ApiResponse.success(SuccessStatus.SEND_MEMBER_BOUGHT_PRODUCTS_SUCCESS, memberProductsIsAvailable);
    }


    @Operation(summary = "{memberId}별 리뷰 조회", description = "{memberId}별 산 매물 기록을 봅니다")
    @GetMapping("/review/{memberId}")
    public ResponseEntity<ApiResponse<PageResponseDto<ReviewResponseDto>>> getMemberReview(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<ReviewResponseDto> memberProductsIsAvailable = memberService.getReviewById(memberId, pageable);

        return ApiResponse.success(SuccessStatus.SEND_MEMBER_BOUGHT_PRODUCTS_SUCCESS, memberProductsIsAvailable);
    }

}
