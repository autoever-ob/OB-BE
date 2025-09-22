package com.campick.server.api.member.controller;

import com.campick.server.api.member.dto.ReviewResponseDto;
import com.campick.server.api.member.service.MemberService;
import com.campick.server.common.dto.PageResponseDto;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/member/review")
@Tag(name = "member-review", description = "멤버 리뷰 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
public class MemberReviewController {

    private final MemberService memberService;

    @Operation(summary = "{memberId}별 리뷰 조회", description = "{memberId}별 산 매물 기록을 봅니다")
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<PageResponseDto<ReviewResponseDto>>> getMemberReview(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<ReviewResponseDto> memberProductsIsAvailable = memberService.getReviewById(memberId, pageable);

        return ApiResponse.success(SuccessStatus.SEND_MEMBER_BOUGHT_PRODUCTS_SUCCESS, memberProductsIsAvailable);
    }
}
