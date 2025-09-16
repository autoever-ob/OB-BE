package com.campick.server.api.product.controller;

import com.campick.server.api.product.dto.AllProductResponseDto;
import com.campick.server.api.product.dto.ProductCreateRequestDto;
import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Long> createProduct(@RequestBody ProductCreateRequestDto dto) {
        Long productId = productService.createProduct(dto);
        return ResponseEntity.ok(productId);
    }

    @GetMapping
    public List<AllProductResponseDto> getProducts() {
        return productService.findAll();
    }
}
