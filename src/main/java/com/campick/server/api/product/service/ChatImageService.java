package com.campick.server.api.product.service;

import com.campick.server.api.chat.repository.ChatImageRepository;
import com.campick.server.common.storage.FirebaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatImageService {
    private final ChatImageRepository chatImageRepository;
    private final FirebaseStorageService firebaseStorageService;

    @Transactional
    public String uploadImage(Long chatId, MultipartFile file) {
        return firebaseStorageService.uploadChatImage(chatId, file);
    }
}
