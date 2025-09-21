package com.campick.server.common.storage;

import com.campick.server.common.response.ErrorStatus;
import com.google.firebase.cloud.StorageClient;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.coyote.BadRequestException;
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

    public Map<String, String> uploadProfileImage(Long memberId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorStatus.EMPTY_FILE_EXCEPTION.getMessage());
        }

        String ext = extractExtension(file.getOriginalFilename());
        String randomName = UUID.randomUUID().toString();
        String objectName = String.format("profiles/%d/%s%s", memberId, randomName, ext);
        String thumbnailObjectName = String.format("profiles/%d/thumbnails/%s%s", memberId, randomName, ext);


        StorageClient.getInstance().bucket().create(objectName, file.getBytes(), file.getContentType());

        uploadThumbnail(file, thumbnailObjectName, 50, 50);

        String bucket = StorageClient.getInstance().bucket().getName();
        String originalUrl = String.format("https://storage.googleapis.com/%s/%s", bucket, urlEncode(objectName));
        String thumbnailUrl = String.format("https://storage.googleapis.com/%s/%s", bucket, urlEncode(thumbnailObjectName));

        Map<String, String> urls = new HashMap<>();
        urls.put("profileImageUrl", originalUrl);
        urls.put("profileThumbnailUrl", thumbnailUrl);

        return urls;
    }

    public Map<String, String> uploadProductImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty())
            throw new BadRequestException(ErrorStatus.EMPTY_FILE_EXCEPTION.getMessage());

        String ext = extractExtension(file.getOriginalFilename());
        String randomName = UUID.randomUUID().toString();
        String objectName = String.format("products/%s%s", randomName, ext);
        String thumbnailObjectName = String.format("products/thumbnails/%s%s", randomName, ext);

        // 파이어베이스에 접속하여 objectName 경로로 버킷 생성
        // 각각 경로, 비트열, 파일 확장자
        StorageClient.getInstance().bucket().create(objectName, file.getBytes(), file.getContentType());

        // 썸네일 작업을 위한 메소드
        uploadThumbnail(file, thumbnailObjectName, 200, 200);

        // 해당 버킷의 경로 이름을 얻어옴
        // 파일 경로를 유니코드로 변환
        String bucket = StorageClient.getInstance().bucket().getName();
        String originalUrl = String.format("https://storage.googleapis.com/%s/%s", bucket, urlEncode(objectName));
        String thumbnailUrl = String.format("https://storage.googleapis.com/%s/%s", bucket, urlEncode(thumbnailObjectName));

        Map<String, String> urls = new HashMap<>();
        urls.put("productImageUrl", originalUrl);
        urls.put("productThumbnailUrl", thumbnailUrl);

        return urls;
    }

    // 썸네일 생성 메소드
    private void uploadThumbnail(MultipartFile file, String objectName, int width, int height) throws IOException {
        // 썸네일 출력을 위한 OutputStream 객체를 생성
        ByteArrayOutputStream thumbnailOStream = new ByteArrayOutputStream();

        // file의 InputStream을 열어서 원하는 크기로 지정해서
        // OutputStream으로 출력
        Thumbnails.of(file.getInputStream())
                .size(width, height)
                .toOutputStream(thumbnailOStream);

        // byte배열로 저장
        byte[] thumbnailBytes = thumbnailOStream.toByteArray();

        // 썸네일도 저장
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
