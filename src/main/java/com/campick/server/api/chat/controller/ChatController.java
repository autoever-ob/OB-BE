package com.campick.server.api.chat.controller;

import com.campick.server.api.chat.entity.ChatMessage;
import com.campick.server.api.chat.entity.ChatRoom;
import com.campick.server.api.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/rooms")
    public List<ChatRoom> getRooms() {
        return chatService.findRooms();
    }

    @GetMapping("/messages")
    public List<ChatMessage> getMessages() {
        return chatService.findMessages();
    }
}
