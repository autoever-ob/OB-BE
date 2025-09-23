package com.campick.server.api.member.controller;

import com.campick.server.api.member.dto.MemberPasswordCheckRequestDto;
import com.campick.server.api.member.dto.MemberResponseDto;
import com.campick.server.api.member.dto.MemberUpdateRequestDto;
import com.campick.server.api.member.dto.PasswordUpdateRequestDto;
import com.campick.server.api.member.dto.ProfileImageUpdateResponseDto;
import com.campick.server.api.member.service.MemberService;
import com.campick.server.common.config.security.SecurityMember;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequestMapping("/api/member")
@Tag(name = "member", description = "멤버 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

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
    @GetMapping("/info/{id}")
    public ResponseEntity<ApiResponse<MemberResponseDto>> getMemberById(@PathVariable Long id) {
        MemberResponseDto dto = memberService.getMemberById(id);
        return ApiResponse.success(SuccessStatus.SEND_FOLLOWING_LIST_SUCCESS, dto);
    }

    @Operation(summary = "로그인된 사용자 비밀번호 확인", description = "로그인된 사용자의 비밀번호를 확인합니다")
    @PostMapping("/check/password")
    public ResponseEntity<ApiResponse<Boolean>> checkPasswordValidation(@AuthenticationPrincipal SecurityMember securityMember,
                                                                        @RequestBody MemberPasswordCheckRequestDto requestDto) {
        boolean isValidation = memberService.checkPasswordValidation(securityMember.getId(), requestDto.getPassword());
        return ApiResponse.success(SuccessStatus.CHECK_PASSWORD_VALIDATION, isValidation);
    }
}
