package com.campick.server.api.product.service;

import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.entity.ProductImage;
import com.campick.server.api.product.repository.ProductImageRepository;
import com.campick.server.api.product.repository.ProductRepository;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.response.ErrorStatus;
import com.campick.server.common.storage.FirebaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final FirebaseStorageService firebaseStorageService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @Transactional
    public List<Map<String, String>> uploadImage(
//            Long productId,
                                                 List<MultipartFile> files) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new NotFoundException(ErrorStatus.PRODUCT_NOT_FOUND.getMessage()));

        List<Map<String, String>> imageUrls = new ArrayList<>();
//        boolean isFirst = true;

        for (MultipartFile file : files) {
            Map<String, String> urls = firebaseStorageService.uploadProductImage(file);
            imageUrls.add(urls);

//            ProductImage productImage = ProductImage.builder()
//                    .product(product)
//                    .imageUrl(urls.get("productImageUrl"))
//                    .thumbnailUrl(urls.get("productThumbnailUrl"))
//                    .isThumbnail(isFirst)
//                    .build();
//            productImageRepository.save(productImage);
//            isFirst = false;
        }

        return imageUrls;
    }
}
