package com.campick.server.common.config.websocket;

import com.campick.server.api.chat.service.ChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<Long, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private final ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserId(session);
        activeSessions.put(userId, session);
        System.out.println("웹소켓 연결: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("클라이언트 메시지: " + payload);

        JsonNode jsonNode = objectMapper.readTree(payload);
        String event = jsonNode.get("type").asText();
        JsonNode data = jsonNode.get("data");

        switch (event) {
            case "join_room":
                chatService.setChatRoomMap(session);
            case "chat_message":
                chatService.handleChatMessage(data);
                break;
            default:
                log.warn("Unknown event: {}", event);
        }


    }

    // 예시: 세션에서 사용자 ID 가져오기
    private Long getUserId(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        activeSessions.remove(getUserId(session));
        System.out.println("웹소켓 연결 종료: " + session.getId());
    }
}