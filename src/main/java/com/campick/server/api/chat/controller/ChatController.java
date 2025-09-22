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
        List<ChatListDto> chatRoom = new ArrayList<>();

        ChatListDto chatListDto = new ChatListDto();
        chatListDto.setChatRoomId(1L);
        chatListDto.setProductName("모터홈 스타렉스 오토");
        chatListDto.setNickname("김대환");
        chatListDto.setLastMessage("한번만 깎아주시면 안될까요");
        chatListDto.setLastMessageCreatedAt(LocalDateTime.now());
        chatListDto.setUnreadMessage(3);

        ChatListDto chatListDto2 = new ChatListDto();
        chatListDto2.setChatRoomId(2L);
        chatListDto2.setProductName("카라반 수동");
        chatListDto2.setNickname("권육윤");
        chatListDto2.setLastMessage("이거 수리 필요한 것 같은데 어떻게 생각하세요? 아니면 ㄴ어쩌아고나섲쩌고 긴메세지를 보내면 어떻게 될까하는 그런 긴 메시지입니다 별로 안 긴가?");
        chatListDto2.setLastMessageCreatedAt(LocalDateTime.now());
        chatListDto2.setUnreadMessage(1);

        chatRoom.add(chatListDto);
        chatRoom.add(chatListDto2);

        myChatResDto.setChatRoom(chatRoom);
        myChatResDto.setTotalUnreadMessage(12);

        return ApiResponse.success(SuccessStatus.SEND_MY_CHATROOMS, myChatResDto);
    }
}
