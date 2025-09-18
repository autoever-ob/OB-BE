package com.campick.server.api.chat.controller;

import com.campick.server.api.chat.dto.ChatRoomReqDto;
import com.campick.server.api.chat.dto.ChatRoomResDto;
import com.campick.server.api.chat.dto.ChatStartResDto;
import com.campick.server.api.chat.entity.ChatMessage;
import com.campick.server.api.chat.entity.ChatRoom;
import com.campick.server.api.chat.service.ChatService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<ChatStartResDto>> startChat(@RequestBody ChatRoomReqDto chatRoomReqDto) {
        Long userId = 1L;
        return ApiResponse.success(SuccessStatus.SEND_CHAT_CREATED, chatService.startChatRoom(chatRoomReqDto, userId));
    }
}
