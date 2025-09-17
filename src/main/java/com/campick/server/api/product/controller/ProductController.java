package com.campick.server.api.product.controller;

import com.campick.server.api.product.dto.AllProductResponseDto;
import com.campick.server.api.product.dto.ProductCreateRequestDto;
import com.campick.server.api.product.service.ProductService;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createProduct(@RequestPart("dto") ProductCreateRequestDto dto,
                                              @RequestPart("images") List<MultipartFile> images,
                                              @RequestPart("mainImage") MultipartFile mainImage) throws IOException {
        Long productId = productService.createProduct(dto, images, mainImage);
        return ResponseEntity.ok(productId);
    }

    @GetMapping
    public List<AllProductResponseDto> getProducts() {
        return productService.findAll();
    }
}
