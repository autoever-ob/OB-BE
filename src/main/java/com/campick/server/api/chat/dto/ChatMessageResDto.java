package com.campick.server.api.chat.dto;

import com.campick.server.api.chat.entity.ChatMessage;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@AllArgsConstructor @Builder
public class ChatMessageResDto {
    private Long chatId;
    private String message;
    private Long senderId;
    private String sendAt;
    private Boolean isRead;
}