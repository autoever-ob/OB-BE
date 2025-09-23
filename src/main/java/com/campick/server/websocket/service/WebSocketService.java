package com.campick.server.websocket.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class WebSocketService {
    private final ConcurrentHashMap<Long, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    public void setActiveSession(Long memberId, WebSocketSession session) {
        activeSessions.put(memberId, session);
    }

    public void removeActiveSession(Long memberId) {
        activeSessions.remove(memberId);
    }

    public WebSocketSession getActiveSession(Long memberId) {
        return activeSessions.get(memberId);
    }
}
