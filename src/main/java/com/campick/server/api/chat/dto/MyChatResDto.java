package com.campick.server.api.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class MyChatResDto {
    private List<ChatListDto> chatRoom;
    private Integer totalUnreadMessage;
}