package com.campick.server.common.config.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    // 앱 접속 시 유저 ID -> WebSocket 세션
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    // 채팅 접속 시 방 ID -> 세션 Set
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    // 앱 접속
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = getUserId(session); // JWT 또는 세션에서 추출
        userSessions.put(userId, session);
        System.out.println("웹소켓 연결됨: " + userId);
    }

    // 앱 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        userSessions.values().remove(session);
        roomSessions.values().forEach(sessions -> sessions.remove(session));
        System.out.println("연결 종료: " + session.getId());
    }

    // 메시지 수신
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // JSON 예시: {"type":"JOIN_ROOM","roomId":"123"}
        String payload = message.getPayload();
        Map<String, String> msg = parseJson(payload); // 간단한 JSON 파싱
        String type = msg.get("type");

        if("JOIN_ROOM".equals(type)){
            joinRoom(msg.get("roomId"), getUserId(session));
        } else if("MESSAGE".equals(type)){
            sendMessage(msg.get("roomId"), msg.get("content"));
        }
    }

    // 방 참여
    public void joinRoom(String roomId, String userId) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null) {
            roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
            System.out.println("📦 " + userId + " 방 참여: " + roomId);
        }
    }

    // 메시지 전송
    public void sendMessage(String roomId, String content) throws Exception {
        for(WebSocketSession s : roomSessions.getOrDefault(roomId, Collections.emptySet())){
            if(s.isOpen()) s.sendMessage(new TextMessage(content));
        }
    }

    // 특정 유저 알림
    public void sendNotification(String userId, String notification) throws Exception {
        WebSocketSession session = userSessions.get(userId);
        if(session != null && session.isOpen()){
            session.sendMessage(new TextMessage(notification));
        }
    }

    private String getUserId(WebSocketSession session){
        // JWT에서 userId 추출하거나 세션 쿼리 파라미터 사용
        return (String) session.getAttributes().get("userId");
    }

    private Map<String, String> parseJson(String json){
        // 간단하게 key:value 형태 파싱 (라이브러리 사용 가능)
        Map<String, String> map = new HashMap<>();
        json = json.replaceAll("[{}\"]", "");
        for(String kv : json.split(",")){
            String[] pair = kv.split(":");
            map.put(pair[0].trim(), pair[1].trim());
        }
        return map;
    }
}
