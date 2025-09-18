package com.campick.server.api.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor
public class ChatMessageReqDto {
    private Long chatId;
    private String content;
    private Long senderId;
}