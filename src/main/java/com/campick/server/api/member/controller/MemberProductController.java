package com.campick.server.api.member.controller;

import com.campick.server.api.member.dto.ProductAllSummaryDto;
import com.campick.server.api.member.dto.ProductAvailableSummaryDto;
import com.campick.server.api.member.dto.TransactionResponseDto;
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

@RequestMapping("/api/member")
@Tag(name = "member-product", description = "멤버 상품/거래 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
public class MemberProductController {

    private final MemberService memberService;

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

    @Operation(summary = "{memberId}별 판 매물 조회", description = "{memberId}별 판 매물 기록을 봅니다 (출력된 memberId는 산 사람)")
    @GetMapping("/product/sold/{memberId}")
    public ResponseEntity<ApiResponse<PageResponseDto<TransactionResponseDto>>> getMemberSold(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<TransactionResponseDto> memberProductsIsAvailable = memberService.getMemberSold(memberId, pageable);

        return ApiResponse.success(SuccessStatus.SEND_MEMBER_SOLD_PRODUCTS_SUCCESS, memberProductsIsAvailable);
    }


    @Operation(summary = "{memberId}별 산 매물 조회", description = "{memberId}별 산 매물 기록을 봅니다 (출력된 memberId는 판 사람)")
    @GetMapping("/product/bought/{memberId}")
    public ResponseEntity<ApiResponse<PageResponseDto<TransactionResponseDto>>> getMemberBought(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<TransactionResponseDto> memberProductsIsAvailable = memberService.getMemberBought(memberId, pageable);

        return ApiResponse.success(SuccessStatus.SEND_MEMBER_BOUGHT_PRODUCTS_SUCCESS, memberProductsIsAvailable);
    }
}
