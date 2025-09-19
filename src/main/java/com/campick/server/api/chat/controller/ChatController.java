package com.campick.server.api.chat.controller;

import com.campick.server.api.chat.dto.*;
import com.campick.server.api.chat.entity.ChatMessage;
import com.campick.server.api.chat.entity.ChatRoom;
import com.campick.server.api.chat.service.ChatService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<ChatStartResDto>> startChat(@RequestBody ChatRoomReqDto chatRoomReqDto) {
        ChatStartResDto chatStartResDto = new ChatStartResDto();

        chatStartResDto.setChatId(1L);

        return ApiResponse.success(SuccessStatus.SEND_CHAT_CREATED, chatStartResDto);
        //return ApiResponse.success(SuccessStatus.SEND_CHAT_CREATED, chatService.startChatRoom(chatRoomReqDto, userId));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatRoomResDto>> getChatRoom(@PathVariable Long chatRoomId) {
        ChatRoomResDto chatRoomResDto = new ChatRoomResDto();

        chatRoomResDto.setIsActive(true);
        chatRoomResDto.setSellerPhoneNumber("010-1234-5678");
        chatRoomResDto.setSellerNickname("믿음을 팝니다");
        chatRoomResDto.setBuyerNickname("김대환");
        chatRoomResDto.setSellerId(1L);
        chatRoomResDto.setBuyerId(2L);
        chatRoomResDto.setProductTitle("모터홈 스타렉스 오토");
        chatRoomResDto.setProductPrice("40000000");
        chatRoomResDto.setProductStatus("AVAILABLE");
        chatRoomResDto.setChatData(null);

        return ApiResponse.success(SuccessStatus.SEND_LOAD_CHATROOM, chatRoomResDto);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<MyChatResDto>> getMyChatRoom() {
        MyChatResDto myChatResDto = new MyChatResDto();

        myChatResDto.setChatRoomId(1L);
        myChatResDto.setProductName("모터홈 스타렉스 오토");
        myChatResDto.setNickname("김대환");
        myChatResDto.setLastMessage("한번만 깎아주시면 안될까요");
        myChatResDto.setLastMessageCreatedAt(LocalDateTime.now());
        myChatResDto.setUnreadMessage(3);
        myChatResDto.setTotalUnreadMessage(12);
        myChatResDto.setIsActive(true);

        return ApiResponse.success(SuccessStatus.SEND_MY_CHATROOMS, myChatResDto);
    }
}
