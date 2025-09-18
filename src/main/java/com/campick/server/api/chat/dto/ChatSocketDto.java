package com.campick.server.api.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ChatSocketDto {
    private Long chatId; // 채팅방 ID
    private Long sellerId;
    private Long buyerId;
    private LocalDateTime createdAt; // 채팅방 생성 시간

    @JsonIgnore // 이 어노테이션으로 WebSocketSession의 직렬화를 방지
    private Set<WebSocketSession> sessions; // 채팅방에 입장한 세션 정보를 담을 Set
    // 세션 수가 0인지 확인하는 메서드
    public boolean isSessionEmpty() {
        return this.sessions.isEmpty();
    }

    @Builder // 빌더 패턴 적용
    public ChatSocketDto(Long chatId, String name, Long sellerId, Long buyerId, LocalDateTime createdAt) {
        this.chatId = chatId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
        // 동시성 문제를 해결하기 위해 ConcurrentHashMap 사용
        this.sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }
}