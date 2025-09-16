package com.campick.server.api.member.controller;

import com.campick.server.api.member.dto.MemberSignUpRequestDto;
import com.campick.server.api.member.service.MemberService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
public class MemberController {

    private MemberService memberService;

    @Operation(summary = "이메일 회원가입 API", description = "회원 정보를 받아 회원가입 합니다.")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody MemberSignUpRequestDto requestDto){
        memberService.signUp(requestDto);
        return ApiResponse.success_only(SuccessStatus.SEND_REGISTER_SUCCESS);
    }
}
