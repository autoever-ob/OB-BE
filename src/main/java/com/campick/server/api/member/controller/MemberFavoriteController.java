package com.campick.server.api.member.controller;


import com.campick.server.api.favorite.service.FavoriteService;
import com.campick.server.api.member.dto.ProductAllSummaryDto;
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
@Tag(name="member-favorite", description = "멤버의 좋아요 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
public class MemberFavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "{memberId}가 좋아요 누른 상품 목록 조회")
    @GetMapping("/favorite/{memberId}")
    public ResponseEntity<ApiResponse<PageResponseDto<ProductAllSummaryDto>>> getMyFavoriteProducts(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<ProductAllSummaryDto> favoriteProducts = favoriteService.getFavoriteProducts(memberId, pageable);
        return ApiResponse.success(SuccessStatus.SEND_MEMBER_FAVORITE_PRODUCTS_SUCCESS, favoriteProducts);
    }
}
