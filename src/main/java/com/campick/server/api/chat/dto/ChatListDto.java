package com.campick.server.api.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor

public class ChatListDto {
    private Long chatRoomId;
    private String productName;
    private String productThumbnail;
    private String nickname;
    private String profileImage;
    private String lastMessage;
    private LocalDateTime lastMessageCreatedAt;
    private Integer unreadMessage;
}
