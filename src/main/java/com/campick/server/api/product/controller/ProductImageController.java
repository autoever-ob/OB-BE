package com.campick.server.api.product.controller;

import com.campick.server.api.product.dto.ProductImageReqDto;
import com.campick.server.api.product.service.ProductImageService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import com.campick.server.common.storage.FirebaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/product/image")
@RequiredArgsConstructor
public class ProductImageController {
    private final ProductImageService productImageService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<String>>> upload(@RequestBody ProductImageReqDto dto) throws IOException {
        return ApiResponse.success(SuccessStatus.UPLOAD_PRODUCT_IMAGE_SUCCESS, productImageService.uploadImage(dto));
    }
}
