package com.campick.server.api.product.controller;

import com.campick.server.api.member.entity.Member;
import com.campick.server.api.model.entity.Model;
import com.campick.server.api.model.repository.ModelRepository;
import com.campick.server.api.option.entity.CarOption;
import com.campick.server.api.option.repository.CarOptionRepository;
import com.campick.server.api.product.dto.*;
import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.repository.ProductRepository;
import com.campick.server.api.product.service.ProductService;
import com.campick.server.api.type.entity.Type;
import com.campick.server.api.type.repository.TypeRepository;
import com.campick.server.common.config.security.SecurityMember;
import com.campick.server.common.dto.PageResponseDto;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.ErrorStatus;
import com.campick.server.common.response.SuccessStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final TypeRepository typeRepository;
    private final ModelRepository modelRepository;
    private final CarOptionRepository carOptionRepository;
    private final ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProduct(@RequestBody ProductCreateReqDto dto,
                                                           @AuthenticationPrincipal SecurityMember securityMember) {
        Long memberId = securityMember.getId();
        return ApiResponse.success(SuccessStatus.SEND_PRODUCT_CREATE_SUCCESS, productService.createProduct(dto, memberId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<ProductResDto>>> getProducts(FilterReqDto filterReqDto,
                                                                                   @RequestParam(defaultValue = "0") Integer page,
                                                                                   @RequestParam(defaultValue = "10") Integer size,
                                                                                   @RequestParam(defaultValue = "createdAt,desc") String sort,
                                                                                   @AuthenticationPrincipal SecurityMember securityMember) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        String property = sortParams[0];

        Long memberId = securityMember.getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        return ApiResponse.success(SuccessStatus.SEND_PRODUCT_LIST_SUCCESS, productService.getProducts(filterReqDto, pageable, memberId));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailResDto>> getCarDetail(@AuthenticationPrincipal SecurityMember securityMember, @PathVariable Long productId) {
        Long memberId = securityMember.getId();

        return ApiResponse.success(SuccessStatus.SEND_PRODUCT_DETAIL_SUCCESS, productService.getProductDetail(memberId, productId));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<Long>> updateProduct(@RequestBody ProductUpdateReqDto dto, @PathVariable Long productId) {
        return ApiResponse.success(SuccessStatus.SEND_PRODUCT_UPDATE_SUCCESS, productService.updateProduct(productId, dto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ApiResponse.success_only(SuccessStatus.SEND_PRODUCT_DELETE_SUCCESS);
    }

    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse<RecommendResDto>> getRecommend(@AuthenticationPrincipal SecurityMember securityMember) {
        Long memberId = securityMember.getId();
        return ApiResponse.success(SuccessStatus.SEND_RECOMMEND_SUCCESS, productService.getRecommend(memberId));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<InfoResDto>> getInfo() {
        InfoResDto infoResDto = new InfoResDto();
        List<Type> type = typeRepository.findAll();
        List<Model> model = modelRepository.findAll();
        List<CarOption> option = carOptionRepository.findAll();

        List<String> stringType = type.stream()
                .map(t -> t.getTypeName().getKorean()).toList();
        List<String> stringModel = model.stream().map(Model::getModelName).toList();
        List<String> stringOption = option.stream().map(CarOption::getName).toList();

        infoResDto.setType(stringType);
        infoResDto.setModel(stringModel);
        infoResDto.setOption(stringOption);

        return ApiResponse.success(SuccessStatus.SEND_INFO_LIST_SUCCESS, infoResDto);
    }

    @PatchMapping("/{productId}/like")
    public ResponseEntity<ApiResponse<Void>> likeProductToggle(@PathVariable Long productId, @AuthenticationPrincipal SecurityMember securityMember) {
        Long memberId = securityMember.getId();

        productService.likeToggle(productId, memberId);

        return ApiResponse.success_only(SuccessStatus.SEND_PRODUCT_LIKE_SUCCESS);
    }

    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<Void>> updateProductStatus(@RequestBody StatusReqDto dto, @AuthenticationPrincipal SecurityMember securityMember) {
        Long memberId = securityMember.getId();

        productService.updateProductStatus(dto, memberId);

        return ApiResponse.success_only(SuccessStatus.SEND_PRODUCT_STATUS_UPDATED);
    }
}
