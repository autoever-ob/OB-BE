package com.campick.server.common.storage;

import com.campick.server.common.response.ErrorStatus;
import com.google.firebase.cloud.StorageClient;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    public String uploadProfileImage(Long memberId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorStatus.EMPTY_FILE_EXCEPTION.getMessage());
        }

        String ext = extractExtension(file.getOriginalFilename());
        String objectName = String.format("profiles/%d/%s%s", memberId, UUID.randomUUID(), ext);

        StorageClient.getInstance().bucket().create(objectName, file.getBytes(), file.getContentType());

        String bucket = StorageClient.getInstance().bucket().getName();
        return String.format("https://storage.googleapis.com/%s/%s", bucket, urlEncode(objectName));
    }

    public String uploadProductImage(Long productId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Empty file");
        }

        String ext = extractExtension(file.getOriginalFilename());
        String objectName = String.format("products/%d/%s%s", productId, UUID.randomUUID(), ext);

        StorageClient.getInstance().bucket().create(objectName, file.getBytes(), file.getContentType());

        String bucket = StorageClient.getInstance().bucket().getName();
        return String.format("https://storage.googleapis.com/%s/%s", bucket, urlEncode(objectName));
    }

    private String extractExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx) : "";
    }

    private String urlEncode(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
