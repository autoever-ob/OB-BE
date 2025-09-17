package com.campick.server.api.member.controller;

import com.campick.server.api.member.dto.EmailVerificationCodeRequestDto;
import com.campick.server.api.member.dto.EmailVerificationRequestDto;
import com.campick.server.api.member.dto.MemberSignUpRequestDto;
import com.campick.server.api.member.service.EmailService;
import com.campick.server.api.member.service.MemberService;
import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.ErrorStatus;
import com.campick.server.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RequestMapping("/api/member")
@Tag(name="member", description = "멤버 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;

    //MOCK
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserMock.UserResponseDto>> getProfile() {
        return ApiResponse.success(SuccessStatus.SEND_MY_PROFILE_SUCCESS, UserMock.getUserInfo());
    }

    @GetMapping("/sold")
    public ResponseEntity<ApiResponse<Page<MySoldMock.ProductDto>>> getMySoldProducts() {
        return ApiResponse.success(SuccessStatus.SEND_MY_SOLD_SUCCESS, MySoldMock.getMyProducts());
    }

    @GetMapping("/selling")
    public ResponseEntity<ApiResponse<Page<MySellMock.SellProductDto>>> getMySellingProducts() {
        return ApiResponse.success(SuccessStatus.SEND_MY_SELL_SUCCESS, MySellMock.getMyProducts());
    }

    @GetMapping("/bought")
    public ResponseEntity<ApiResponse<Page<MyBoughtMock.Product>>> getMyBoughtProducts() {
        return ApiResponse.success(SuccessStatus.SEND_MY_BOUGHT_SUCCESS, MyBoughtMock.getPurchasedProducts());
    }

    @GetMapping("/review/{userId}")
    public ResponseEntity<ApiResponse<Page<MyReviewMock.Review>>> getReviews() {
        return ApiResponse.success(SuccessStatus.SEND_REVIEW_SUCCESS, MyReviewMock.getReviews());
    }


//    @Operation(summary = "이메일 회원가입 API", description = "회원 정보를 받아 회원가입 합니다.")
//    @PostMapping("/signup")
//    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody MemberSignUpRequestDto requestDto){
//        memberService.signUp(requestDto);
//        return ApiResponse.success_only(SuccessStatus.SEND_REGISTER_SUCCESS);
//    }
//
//    @Operation(summary = "이메일 회원가입 API", description = "회원 정보를 받아 회원가입 합니다.")
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody MemberSignUpRequestDto requestDto){
//        memberService.signUp(requestDto);
//        return ApiResponse.success_only(SuccessStatus.SEND_REGISTER_SUCCESS);
//    }

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
    @PostMapping("/verify-email")
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
    @PostMapping("/verification-email-code")
    public ResponseEntity<ApiResponse<Void>> verificationByCode(@RequestBody EmailVerificationCodeRequestDto emailVerificationCodeRequestDto) {
        LocalDateTime requestedAt = LocalDateTime.now();
        emailService.verifyEmail(emailVerificationCodeRequestDto.getCode(), requestedAt);
        return ApiResponse.success_only(SuccessStatus.SEND_EMAIL_VERIFICATION_SUCCESS);
    }
}
