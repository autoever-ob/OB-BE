package com.campick.server.api.product.service;

import com.campick.server.common.storage.FirebaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final FirebaseStorageService firebaseStorageService;

    public List<Map<String, String>> uploadImage(List<MultipartFile> files) throws IOException {
        List<Map<String, String>> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            imageUrls.add(firebaseStorageService.uploadProductImage(file));
        }

        return imageUrls;
    }
}
