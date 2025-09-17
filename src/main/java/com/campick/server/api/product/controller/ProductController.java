package com.campick.server.api.product.controller;

import com.campick.server.api.product.dto.AllProductResponseDto;
import com.campick.server.api.product.dto.ProductCreateRequestDto;
import com.campick.server.api.product.dto.ProductCreateWithImageRequestDto;
import com.campick.server.api.product.service.ProductService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProduct(@RequestBody ProductCreateRequestDto dto) {
        Long productId = productService.createProduct(dto);
        return ApiResponse.success(SuccessStatus.SEND_PRODUCT_CREATE_SUCCESS, productId);

    }
  
    @PostMapping(name="create-with-images",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createProductWithImages(@RequestPart("dto") ProductCreateWithImageRequestDto dto,
                                              @RequestPart("images") List<MultipartFile> images,
                                              @RequestPart("mainImage") MultipartFile mainImage) throws IOException {
        Long productId = productService.createProductWithImages(dto, images, mainImage);
        return ResponseEntity.ok(productId);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AllProductResponseDto>>> getProducts() {
        List<AllProductResponseDto> productsList = productService.findAll();
        return ApiResponse.success(SuccessStatus.SEND_PRODUCT_LIST_SUCCESS, productsList);
    }
}
