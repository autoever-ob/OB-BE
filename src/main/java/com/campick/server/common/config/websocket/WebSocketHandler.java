package com.campick.server.common.config.websocket;

import com.campick.server.api.chat.service.ChatService;
import com.campick.server.websocket.service.WebSocketService;
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
    private final ChatService chatService;
    private final WebSocketService webSocketService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long memberId = getMemberId(session);
        webSocketService.setActiveSession(memberId, session);
        System.out.println("웹소켓 연결: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("클라이언트 메시지: " + payload);

        Long memberId = getMemberId(session);

        JsonNode jsonNode = objectMapper.readTree(payload);
        String event = jsonNode.get("type").asText();
        JsonNode data = jsonNode.get("data");

        switch (event) {
            case "start_room":
                chatService.startChatRoom(session, data);
                break;
            case "set_chat_room":
                chatService.setChatRoomMap(memberId, session, data);
                break;
            case "chat_message":
                chatService.handleChatMessage(session, data);
                break;
            case "sold":
                chatService.broadcastSoldEvent(session, data);
                break;
            case "is_online":
                webSocketService.checkIsOnline(session, memberId, data);
                break;
            default:
                log.warn("Unknown event: {}", event);
        }


    }

    // 예시: 세션에서 사용자 ID 가져오기
    private Long getMemberId(WebSocketSession session) {
        return (Long) session.getAttributes().get("memberId");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long memberId = getMemberId(session);
        webSocketService.removeActiveSession(memberId);
        chatService.removeFromChatRoomMap(memberId, session);
        System.out.println("웹소켓 연결 종료: " + session.getId());
    }
}