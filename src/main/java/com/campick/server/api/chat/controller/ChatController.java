package com.campick.server.api.chat.controller;

import com.campick.server.api.chat.dto.*;
import com.campick.server.api.chat.entity.ChatMessage;
import com.campick.server.api.chat.entity.ChatRoom;
import com.campick.server.api.chat.service.ChatService;
import com.campick.server.common.config.security.SecurityMember;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<Long>> startChat(@RequestBody ChatRoomReqDto chatRoomReqDto,
                                                       @AuthenticationPrincipal SecurityMember securityMember) {
        Long memberId = securityMember.getId();

        return ApiResponse.success(SuccessStatus.SEND_CHAT_CREATED, chatService.startChatRoom(chatRoomReqDto, memberId));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatRoomResDto>> getChatRoom(@PathVariable Long chatRoomId) {
        return ApiResponse.success(SuccessStatus.SEND_LOAD_CHATROOM, chatService.getChatRoom(chatRoomId));
    }

    @PatchMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> readChatRoom(@PathVariable Long chatRoomId,
                                                          @AuthenticationPrincipal SecurityMember securityMember) {
        Long memberId =  securityMember.getId();
        chatService.readChatRoom(chatRoomId, memberId);
        return ApiResponse.success_only(SuccessStatus.READ_CHAT_SUCCESS);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<MyChatResDto>> getMyChatRoom(@AuthenticationPrincipal SecurityMember securityMember) {
        Long memberId = securityMember.getId();

        return ApiResponse.success(SuccessStatus.SEND_MY_CHATROOMS, chatService.getMyChatRooms(memberId));
    }

    @GetMapping("/totalUnreadMessage")
    public ResponseEntity<ApiResponse<Integer>> getTotalUnreadMessage(@AuthenticationPrincipal SecurityMember securityMember) {
        Long memberId = securityMember.getId();
        return ApiResponse.success(SuccessStatus.SEND_TOTAL_UNREAD_MSG, chatService.getTotalUnreadMessage(memberId));
    }

    @DeleteMapping("/complete/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> completeChat(@PathVariable Long chatRoomId,
                                                          @AuthenticationPrincipal SecurityMember securityMember) {
        Long memberId = securityMember.getId();
        chatService.completeChat(chatRoomId, memberId);
        return ApiResponse.success_only(SuccessStatus.COMPLETE_CHAT);
    }
}
