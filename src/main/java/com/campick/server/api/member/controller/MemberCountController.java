package com.campick.server.api.member.controller;

import com.campick.server.api.member.service.CountService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/count")
@Tag(name="member-count", description = "멤버 제품 개수 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
public class MemberCountController{

    private final CountService countService;

    @Operation(summary = "{memberId}별 모든 매물 개수 조회", description = "{memberId}별 모든 매물 개수 조회합니다")
    @GetMapping("/product/sell-or-reserve/{memberId}")
    public ResponseEntity<ApiResponse<Integer>> getMemberAllProductCount(@PathVariable Long memberId){

        return ApiResponse.success(SuccessStatus.SEND_MEMBER_ALL_PRODUCTS_COUNT_SUCCESS, countService.getMemberAllProductCount(memberId));
    }

    @Operation(summary = "{memberId}별 등록된 판매중인 매물 개수 조회", description = "{memberId}별 판매중인 매물 개수 조회합니다")
    @GetMapping("/product/sell-or-reserve/{memberId}")
    public ResponseEntity<ApiResponse<Integer>> getMemberProductAvailableCount(@PathVariable Long memberId){

        return ApiResponse.success(SuccessStatus.SEND_MEMBER_AVAILABLE_PRODUCTS_COUNT_SUCCESS, countService.getMemberProductAvailableCount(memberId));
    }

    @Operation(summary = "{memberId}별 판매된 매물 개수 조회", description = "{memberId}별 판매된 매물 개수 조회합니다")
    @GetMapping("/product/sold/{memberId}")
    public ResponseEntity<ApiResponse<Integer>> getMemberProductSoldCount(@PathVariable Long memberId){

        return ApiResponse.success(SuccessStatus.SEND_MEMBER_SOLD_PRODUCTS_COUNT_SUCCESS, countService.getMemberProductSoldCount(memberId));
    }
}