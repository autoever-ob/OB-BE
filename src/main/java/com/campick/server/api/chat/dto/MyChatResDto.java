package com.campick.server.api.chat.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
@AllArgsConstructor
public class MyChatResDto {
    private List<ChatListDto> chatRoom;
    private Integer totalUnreadMessage;
}