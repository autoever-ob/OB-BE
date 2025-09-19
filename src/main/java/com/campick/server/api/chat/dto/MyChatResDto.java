package com.campick.server.api.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class MyChatResDto {
    private Long chatRoomId;
    private String productName;
    private String productThumbnail;
    private String nickname;
    private String profileImage;
    private String lastMessage;
    private LocalDateTime lastMessageCreatedAt;
    private Integer unreadMessage;
    private Integer totalUnreadMessage;
    private Boolean isActive;
}