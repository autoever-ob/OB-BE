package com.campick.server.websocket.service;

import com.campick.server.api.chat.entity.ChatRoom;
import com.campick.server.api.chat.repository.ChatRoomRepository;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.response.ErrorStatus;
import com.campick.server.websocket.dto.IsOnline;
import com.campick.server.websocket.dto.IsOnlineResDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class WebSocketService {
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<Long, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private final ChatRoomRepository chatRoomRepository;

    public void setActiveSession(Long memberId, WebSocketSession session) {
        activeSessions.put(memberId, session);
    }

    public void removeActiveSession(Long memberId) {
        activeSessions.remove(memberId);
    }

    public WebSocketSession getActiveSession(Long memberId) {
        return activeSessions.get(memberId);
    }

    public void checkIsOnline(WebSocketSession session, Long memberId, JsonNode data) throws IOException {
        List<Long> chatIds = objectMapper.convertValue(data.get("chatId"), new TypeReference<>() {
        });
        List<IsOnline> isOnlines = new ArrayList<>();

        for (Long chatId : chatIds) {
            ChatRoom chatRoom = chatRoomRepository.findDetailById(chatId).orElseThrow(
                    () -> new NotFoundException(ErrorStatus.CHAT_NOT_FOUND.getMessage())
            );
            Long otherId = Objects.equals(chatRoom.getSeller().getId(), memberId) ? chatRoom.getBuyer().getId() : chatRoom.getSeller().getId();

            isOnlines.add(
                    IsOnline.builder()
                            .chatId(chatId)
                            .isOnline(activeSessions.get(otherId) != null)
                            .build()
            );
        }
        IsOnlineResDto isOnlineResDto = IsOnlineResDto.builder().online(isOnlines).build();

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "is_online");
        payload.put("data", isOnlineResDto);

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
    }
}
