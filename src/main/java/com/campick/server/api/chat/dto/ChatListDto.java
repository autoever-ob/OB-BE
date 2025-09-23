package com.campick.server.api.chat.dto;

import lombok.*;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor
public class ChatListDto {
    private Long chatRoomId;
    private String productName;
    private String productThumbnail;
    private String nickname;
    private String profileImage;
    private String lastMessage;
    private String lastMessageCreatedAt;
    private Integer unreadMessage;
}
