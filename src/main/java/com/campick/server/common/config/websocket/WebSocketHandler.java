package com.campick.server.common.config.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserId(session);
        activeSessions.put(userId, session);
        System.out.println("웹소켓 연결: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("클라이언트 메시지: " + message.getPayload());
        // 1. 메시지 파싱 (예: JSON → DTO)
        String payload = message.getPayload(); // 클라이언트가 전송한 메시지



        // 모든 세션에 메시지 브로드캐스트
        for (WebSocketSession s : activeSessions.values()) {
            s.sendMessage(new TextMessage("서버에서 응답: " + message.getPayload()));
        }
    }

    // 예시: 세션에서 사용자 ID 가져오기
    private String getUserId(WebSocketSession session) {
        return (String) session.getAttributes().get("userId");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        activeSessions.remove(getUserId(session));
        System.out.println("웹소켓 연결 종료: " + session.getId());
    }
}