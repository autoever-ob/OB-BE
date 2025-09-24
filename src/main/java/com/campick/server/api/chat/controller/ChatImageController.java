package com.campick.server.api.chat.controller;

import com.campick.server.api.chat.dto.ChatImageReqDto;
import com.campick.server.api.chat.dto.ChatImageResDto;
import com.campick.server.api.product.service.ChatImageService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/chat/image")
@RequiredArgsConstructor
public class ChatImageController {
    private final ChatImageService chatImageService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<ChatImageResDto>> upload(@RequestParam("chatId") Long chatId,
                                                               @RequestPart("file") MultipartFile file) {
        String imageUrl = chatImageService.uploadImage(chatId, file);
        ChatImageResDto chatImageResDto = ChatImageResDto.builder()
                .chatImageUrl(imageUrl).build();
        return ApiResponse.success(SuccessStatus.UPLOAD_CHAT_IMAGE_SUCCESS, chatImageResDto);
    }
}
