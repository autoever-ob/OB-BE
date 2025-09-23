package com.campick.server.api.chat.service;

import com.campick.server.api.chat.dto.*;
import com.campick.server.api.chat.entity.ChatMessage;
import com.campick.server.api.chat.entity.ChatRoom;
import com.campick.server.api.chat.repository.ChatMessageRepository;
import com.campick.server.api.chat.repository.ChatRoomRepository;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.repository.MemberRepository;
import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.entity.ProductImage;
import com.campick.server.api.product.repository.ProductRepository;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.response.ErrorStatus;
import com.campick.server.common.util.TimeUtil;
import com.campick.server.websocket.service.WebSocketService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final WebSocketService webSocketService;
    private Map<Long, ChatSocketDto> chatRoomMap;

    @PostConstruct
    public void init() {
        this.chatRoomMap = new LinkedHashMap<>();
    }

    public void setChatRoomMap(WebSocketSession session, JsonNode data) throws IOException {
        Long chatId = data.get("chat_id").asLong();

        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.CHAT_NOT_FOUND.getMessage()));

        ChatSocketDto chatSocketDto = ChatSocketDto.builder()
                .chatId(chatId)
                .sellerId(chatRoom.getSeller().getId())
                .buyerId(chatRoom.getBuyer().getId())
                .build();
        chatSocketDto.getSessions().add(session);
        chatSocketDto.getSessions().add(webSocketService.getActiveSession(chatRoom.getSeller().getId()));
        chatRoomMap.put(chatId, chatSocketDto);
    }

    private ChatSocketDto findChatRoomById(Long chatId) {
        return chatRoomMap.get(chatId);
    }

    //http
    public Long startChatRoom(ChatRoomReqDto chatRoomReqDto, Long memberId) {
        Product product = productRepository.findById(chatRoomReqDto.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorStatus.PRODUCT_NOT_FOUND.getMessage()));
        Member seller = product.getSeller();
        Member buyer = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));
        ChatRoom chatRoom = ChatRoom.builder()
                .seller(seller)
                .buyer(buyer)
                .product(product)
                .build();
        chatRoomRepository.save(chatRoom);

        return chatRoom.getId();
    }

    public ChatRoomResDto getChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findDetailById(chatRoomId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.CHAT_NOT_FOUND.getMessage())
        );
        List<ChatMessage> chatMessages = chatMessageRepository.findMessagesByChatRoomId(chatRoomId);

        return convertToChatRoomResDto(chatRoom, chatMessages);
    }

    public MyChatResDto getMyChatRooms(Long memberId) {
        List<ChatRoom> myChatRooms = chatRoomRepository.findAllByMemberId(memberId);
        List<ChatListDto> chatListDtos = myChatRooms.stream().map(
                chatRoom -> {
                    String thumbnailUrl = chatRoom.getProduct().getImages().stream()
                            .filter(ProductImage::getIsThumbnail)
                            .map(ProductImage::getImageUrl)
                            .findFirst()
                            .orElse(null);

                    ChatMessage lastChatMessage = chatMessageRepository.findLastMessageByChatRoomId(chatRoom.getId());
                    Integer unreadMessageCount = chatMessageRepository.countUnreadMessages(chatRoom.getId(), memberId);

                    return ChatListDto.builder()
                            .chatRoomId(chatRoom.getId())
                            .productName(chatRoom.getProduct().getTitle())
                            .productThumbnail(thumbnailUrl)
                            .nickname(chatRoom.getSeller().getNickname())
                            .profileImage(chatRoom.getSeller().getProfileImageUrl())
                            .lastMessage(lastChatMessage.getMessage())
                            .lastMessageCreatedAt(TimeUtil.getTimeAgo(lastChatMessage.getCreatedAt()))
                            .unreadMessage(unreadMessageCount)
                            .build();
                }).toList();

        return MyChatResDto.builder()
                .chatRoom(chatListDtos)
                .totalUnreadMessage(chatMessageRepository.countAllUnreadMessages(memberId))
                .build();
    }

    public Integer getTotalUnreadMessage(Long memberId) {
        return chatMessageRepository.countAllUnreadMessages(memberId);
    }

    public void completeChat(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findDetailById(chatRoomId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.CHAT_NOT_FOUND.getMessage())
        );

        if (memberId.equals(chatRoom.getSeller().getId()))
            chatRoom.setIsSellerOut(true);
        else
            chatRoom.setIsBuyerOut(true);
        chatRoomRepository.save(chatRoom);
    }

    public void handleChatMessage(JsonNode data) {
        ChatMessageReqDto chatMessageReqDto = objectMapper.convertValue(data, ChatMessageReqDto.class);

        ChatMessage chatMessage = saveMessage(chatMessageReqDto);
        ChatMessageResDto chatMessageResDto = convertToChatMessageResDto(chatMessage);
        sendMessage(chatMessageReqDto.getChatId(), chatMessageResDto);
    }

    private ChatMessageResDto convertToChatMessageResDto(ChatMessage chatMessage) {
        return ChatMessageResDto.builder()
                .message(chatMessage.getMessage())
                .senderId(chatMessage.getMember().getId())
                .sendAt(chatMessage.getCreatedAt())
                .isRead(chatMessage.getIsRead())
                .build();
    }

    private ChatMessage saveMessage(ChatMessageReqDto chatMessageReqDto) {
        Long chatId = chatMessageReqDto.getChatId();
        Long senderId = chatMessageReqDto.getSenderId();
        Member seller = memberRepository.findById(senderId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );
        String content = chatMessageReqDto.getContent();

        ChatRoom chatRoom = chatRoomRepository.findById(chatId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.CHAT_NOT_FOUND.getMessage()));
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(seller)
                .message(content)
                .build();
        chatMessageRepository.save(chatMessage);

        return chatMessage;
    }

    //websocket
    public void sendMessage(Long chatId, ChatMessageResDto message) {
        ChatSocketDto room = findChatRoomById(chatId);

        if (room != null) {
            for (WebSocketSession session : room.getSessions()) {
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    public void broadcastSoldEvent(JsonNode data) {
        Long chatId = data.get("chat_id").asLong();
        ChatSocketDto room = findChatRoomById(chatId);
        if (room != null) {
            for (WebSocketSession session : room.getSessions()) {
                try {
                    session.sendMessage(new TextMessage("{\"type\":\"sold\"}"));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private ChatRoomResDto convertToChatRoomResDto(ChatRoom chatRoom, List<ChatMessage> chatMessages) {
        ChatRoomResDto chatRoomResDto = new ChatRoomResDto();
        chatRoomResDto.setSellerId(chatRoom.getSeller().getId());
        chatRoomResDto.setSellerNickname(chatRoom.getSeller().getNickname());
        chatRoomResDto.setBuyerId(chatRoom.getBuyer().getId());
        chatRoomResDto.setBuyerNickname(chatRoom.getBuyer().getNickname());
        chatRoomResDto.setSellerProfileImage(chatRoom.getSeller().getProfileImageUrl());
        chatRoomResDto.setSellerPhoneNumber(chatRoom.getSeller().getMobileNumber());
        chatRoomResDto.setProductId(chatRoom.getProduct().getId());
        chatRoomResDto.setProductTitle(chatRoom.getProduct().getTitle());
        chatRoomResDto.setProductStatus(chatRoom.getProduct().getStatus().toString());
        chatRoomResDto.setProductPrice(chatRoom.getProduct().getCost().toString());
        chatRoomResDto.setIsActive(!chatRoom.getIsSellerOut() && !chatRoom.getIsBuyerOut());

        List<ChatMessageResDto> chatMessageResDto = chatMessages.stream()
                .map(this::convertToChatMessageResDto
                ).toList();
        chatRoomResDto.setChatData(chatMessageResDto);

        return chatRoomResDto;
    }

}
