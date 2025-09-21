package com.campick.server.api.product.controller;

import com.campick.server.api.product.dto.ProductImageUploadResponseDto;
import com.campick.server.api.product.service.ProductImageService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product/image")
@RequiredArgsConstructor
public class ProductImageController {
    private final ProductImageService productImageService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<List<ProductImageUploadResponseDto>>> upload(@RequestPart("files") List<MultipartFile> files) throws IOException {
        List<Map<String, String>> imageUrls = productImageService.uploadImage(files);
        List<ProductImageUploadResponseDto> response = imageUrls.stream()
                .map(ProductImageUploadResponseDto::from)
                .collect(Collectors.toList());
        return ApiResponse.success(SuccessStatus.UPLOAD_PRODUCT_IMAGE_SUCCESS, response);
    }
}
