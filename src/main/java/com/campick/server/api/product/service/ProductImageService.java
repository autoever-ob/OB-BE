package com.campick.server.api.product.service;

import com.campick.server.api.product.dto.ProductImageReqDto;
import com.campick.server.common.storage.FirebaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final FirebaseStorageService firebaseStorageService;

    public String uploadImage(ProductImageReqDto dto) throws IOException {
        return firebaseStorageService.uploadProductImage(dto.getProductId(), dto.getFile());
    }
}
