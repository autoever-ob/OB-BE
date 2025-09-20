package com.campick.server.api.product.service;

import com.campick.server.common.storage.FirebaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final FirebaseStorageService firebaseStorageService;

    public List<String> uploadImage(List<MultipartFile> files) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            imageUrls.add(firebaseStorageService.uploadProductImage(file));
        }

        return imageUrls;
    }
}
