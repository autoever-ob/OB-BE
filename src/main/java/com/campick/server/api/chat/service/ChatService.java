package com.campick.server.api.chat.service;

import com.campick.server.api.chat.dto.ChatMessageResDto;
import com.campick.server.api.chat.dto.ChatSocketDto;
import com.campick.server.api.chat.entity.ChatMessage;
import com.campick.server.api.chat.entity.ChatRoom;
import com.campick.server.api.chat.repository.ChatMessageRepository;
import com.campick.server.api.chat.repository.ChatRoomRepository;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.response.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;
    private Map<Long, ChatSocketDto> chatRoomMap;

    @PostConstruct
    public void init() {
        this.chatRoomMap = new LinkedHashMap<>();
    }

    private ChatSocketDto setAndFindChatRoomMapById(Long chatId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.CHAT_NOT_FOUND.getMessage()));

        ChatSocketDto chatSocketDto = ChatSocketDto.builder()
                .chatId(chatId)
                .sellerId(chatRoom.getSeller().getId())
                .buyerId(chatRoom.getBuyer().getId())
                .build();
        chatRoomMap.put(chatId, chatSocketDto);
        return chatSocketDto;
    }

    private ChatSocketDto findChatRoomById(Long chatId) {
        return chatRoomMap.get(chatId);
    }

    public void startChat(Long chatId, WebSocketSession session) {
        ChatSocketDto room = findChatRoomById(chatId);
        if (room == null) {
            room = setAndFindChatRoomMapById(chatId);
        }

        room.getSessions().add(session);
        System.out.println("New session added: " + session);
    }

    public void sendMessage(Long chatId, ChatMessageResDto message) {
        ChatSocketDto room = findChatRoomById(chatId);
        if (room != null) {
            for (WebSocketSession session : room.getSessions()) {
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    public void broadcastSoldEvent(Long chatId) {
        ChatSocketDto room = findChatRoomById(chatId);
        if (room != null) {
            for (WebSocketSession session : room.getSessions()) {
                try {
                    session.sendMessage(new TextMessage("{\"type\":\"sold\"}"));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

}
