package com.campick.server.common.storage;

import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.exception.ImageUploadFailedException;
import com.campick.server.common.response.ErrorStatus;
import com.google.firebase.cloud.StorageClient;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FirebaseStorageService {


    @Value("https://firebasestorage.googleapis.com/v0/b/campick-6072a.firebasestorage.app")
    private String storageBaseUrl;

    public Map<String, String> uploadProfileImage(Long memberId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorStatus.EMPTY_FILE_EXCEPTION.getMessage());
        }

        try {

//            https://firebasestorage.googleapis.com/v0/b/campick-6072a.firebasestorage.app/o/profiles%2F1%2F43b9d56f-2b62-4d07-87e3-fe596872ab4e.jpg?alt=media&token=f174e790-f7e5-40c1-8cab-cee3aceac61c
//            https://firebasestorage.googleapis.com/v0/b/campick-6072a.firebasestorage.app/o/products%2F03606fef-9c86-479a-953c-fb25461d714b.jpg?alt=media
            String ext = extractExtension(file.getOriginalFilename());
            String randomName = UUID.randomUUID().toString();
            String objectName = String.format("profiles/%d/%s%s?alt=media", memberId, randomName, ext);
            String thumbnailObjectName = String.format("profiles/%d/thumbnails/%s%s?alt=media", memberId, randomName, ext);

            StorageClient.getInstance().bucket().create(objectName, file.getBytes(), file.getContentType());

            uploadThumbnail(file, thumbnailObjectName, 50, 50);

            String bucket = StorageClient.getInstance().bucket().getName();
            String originalUrl = String.format("%s/o/%s?alt=media",storageBaseUrl, urlEncode(objectName));
            String thumbnailUrl = String.format("%s/o/%s?alt=media",storageBaseUrl, urlEncode(thumbnailObjectName));

            Map<String, String> urls = new HashMap<>();
            urls.put("profileImageUrl", originalUrl);
            urls.put("profileThumbnailUrl", thumbnailUrl);

            return urls;
        } catch (IOException e) {
            throw new ImageUploadFailedException(ErrorStatus.IMAGE_UPLOAD_FAILED_EXCEPTION);
        }
    }

    public Map<String, String> uploadProductImage(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new BadRequestException(ErrorStatus.EMPTY_FILE_EXCEPTION.getMessage());

        try {
            String ext = extractExtension(file.getOriginalFilename());
            String randomName = UUID.randomUUID().toString();
            String objectName = String.format("products/%s%s", randomName, ext);
            String thumbnailObjectName = String.format("products/thumbnails/%s%s", randomName, ext);

            StorageClient.getInstance().bucket().create(objectName, file.getBytes(), file.getContentType());

            uploadThumbnail(file, thumbnailObjectName, 200, 200);

            String bucket = StorageClient.getInstance().bucket().getName();
            String originalUrl = String.format("%s/o/%s?alt=media",storageBaseUrl, urlEncode(objectName));
            String thumbnailUrl = String.format("%s/o/%s?alt=media",storageBaseUrl, urlEncode(thumbnailObjectName));

            Map<String, String> urls = new HashMap<>();
            urls.put("productImageUrl", originalUrl);
            urls.put("productThumbnailUrl", thumbnailUrl);

            return urls;
        } catch (IOException e) {
            throw new ImageUploadFailedException(ErrorStatus.IMAGE_UPLOAD_FAILED_EXCEPTION);
        }
    }

    public String uploadChatImage(Long chatId, MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new BadRequestException(ErrorStatus.EMPTY_FILE_EXCEPTION.getMessage());

        try {
            String ext = extractExtension(file.getOriginalFilename());
            String objectName = String.format("chat/%d%s", chatId, ext);

            StorageClient.getInstance().bucket().create(objectName, file.getBytes(), file.getContentType());

            return String.format("%s/o/%s?alt=media",storageBaseUrl, urlEncode(objectName));
        } catch (IOException e) {
            throw new ImageUploadFailedException(ErrorStatus.IMAGE_UPLOAD_FAILED_EXCEPTION);
        }
    }

    private void uploadThumbnail(MultipartFile file, String objectName, int width, int height) throws IOException {
        ByteArrayOutputStream thumbnailOStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(width, height)
                .toOutputStream(thumbnailOStream);

        byte[] thumbnailBytes = thumbnailOStream.toByteArray();

        StorageClient.getInstance().bucket().create(objectName, thumbnailBytes, file.getContentType());
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
