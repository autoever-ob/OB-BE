package com.campick.server.websocket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class IsOnline {
    private Long chatId;
    private Boolean isOnline;
}
