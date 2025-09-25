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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public void setChatRoomMap(Long memberId, WebSocketSession session, JsonNode data) throws IOException {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByMemberId(memberId);
        for (ChatRoom chatRoom : chatRooms) {
            System.out.println(chatRoom);
            findOrMakeChatRoomById(chatRoom.getId(), session);
        }
    }

    private ChatSocketDto findOrMakeChatRoomById(Long chatId, WebSocketSession session) {
        ChatSocketDto room = chatRoomMap.get(chatId);

        if (room == null) {
            ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                    .orElseThrow(() -> new NotFoundException(ErrorStatus.CHAT_NOT_FOUND.getMessage()));

            ChatSocketDto chatSocketDto = ChatSocketDto.builder()
                    .chatId(chatId)
                    .sellerId(chatRoom.getSeller().getId())
                    .buyerId(chatRoom.getBuyer().getId())
                    .build();

            chatSocketDto.getSessions().add(session);

            Long myId = (Long) session.getAttributes().get("memberId");
            Long otherId = Objects.equals(chatRoom.getBuyer().getId(), myId) ? chatRoom.getSeller().getId() : chatRoom.getBuyer().getId();

            WebSocketSession otherSession = webSocketService.getActiveSession(otherId);
            if  (otherSession != null)
                chatSocketDto.getSessions().add(webSocketService.getActiveSession(otherId));
            chatRoomMap.put(chatId, chatSocketDto);
        } else
            room.getSessions().add(session);

        System.out.println(chatRoomMap.get(chatId).getSessions());

        return chatRoomMap.get(chatId);
    }

    public void startChatRoom(WebSocketSession session, JsonNode data) throws IOException {
        Long chatId = data.get("chatId").asLong();

        findOrMakeChatRoomById(chatId, session);
    }

    //http
    public Long startChatRoom(ChatRoomReqDto chatRoomReqDto, Long memberId) {
        Product product = productRepository.findById(chatRoomReqDto.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorStatus.PRODUCT_NOT_FOUND.getMessage()));
        Member seller = product.getSeller();
        Member buyer = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));

        ChatRoom chatRoom = chatRoomRepository.findByProductAndSellerAndBuyer(product, seller, buyer)
                .orElse(null);

        if (chatRoom == null) {
            chatRoom = ChatRoom.builder()
                    .seller(seller)
                    .buyer(buyer)
                    .product(product)
                    .build();
            chatRoomRepository.save(chatRoom);
        }
        return chatRoom.getId();
    }

    public ChatRoomPageResDto<ChatMessageResDto> getChatRoom(Long chatRoomId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findDetailById(chatRoomId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.CHAT_NOT_FOUND.getMessage())
        );
        Page<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable);
        Page<ChatMessageResDto> chatMessageResDtos = chatMessages.map(
                cm -> ChatMessageResDto.builder()
                        .message(cm.getMessage())
                        .senderId(cm.getMember().getId())
                        .sendAt(TimeUtil.getTimeAgo(cm.getCreatedAt()))
                        .isRead(cm.getIsRead())
                        .build()
        );

        return convertToChatRoomResDto(chatRoom, chatMessageResDtos);
    }

    public void readChatRoom(Long chatRoomId, Long memberId) {
        Integer readMessageCount = chatMessageRepository.markMessagesAsRead(chatRoomId, memberId);
    }

    public MyChatPageResDto<ChatListDto> getMyChatRooms(Long memberId, Pageable pageable) {
        Page<ChatRoom> myChatRooms = chatRoomRepository.findByMemberIdOrderByLastMessageDesc(memberId, pageable);
        List<Long> chatRoomIds = myChatRooms.stream().map(ChatRoom::getId).toList();
        Map<Long, ChatMessage> lastMessageMap = chatMessageRepository.findLastMessages(chatRoomIds)
                .stream()
                .collect(Collectors.toMap(m -> m.getChatRoom().getId(), Function.identity()));
        long total = chatRoomRepository.countChatRoomByMemberId(memberId);

        List<ChatListDto> chatListDtos = myChatRooms.stream().map(
                chatRoom -> {
                    String thumbnailUrl = chatRoom.getProduct().getImages().stream()
                            .filter(ProductImage::getIsThumbnail)
                            .map(ProductImage::getImageUrl)
                            .findFirst()
                            .orElse(null);

                    ChatMessage lastChatMessage = lastMessageMap.get(chatRoom.getId());
                    Integer unreadMessageCount = chatMessageRepository.countUnreadMessages(chatRoom.getId(), memberId);

                    return ChatListDto.builder()
                            .chatRoomId(chatRoom.getId())
                            .productName(chatRoom.getProduct().getTitle())
                            .productThumbnail(thumbnailUrl)
                            .nickname(chatRoom.getSeller().getNickname())
                            .profileImage(chatRoom.getSeller().getProfileImageUrl())
                            .lastMessage(lastChatMessage == null ? "" : lastChatMessage.getMessage())
                            .lastMessageCreatedAt(lastChatMessage == null ? "" : TimeUtil.getTimeAgo(lastChatMessage.getCreatedAt()))
                            .unreadMessage(unreadMessageCount)
                            .build();
                }).toList();

        Integer totalUnreadMessage = chatMessageRepository.countAllUnreadMessages(memberId);
        Page<ChatListDto> pageImpl = new PageImpl<>(chatListDtos, pageable, total);

        return new MyChatPageResDto<>(pageImpl, totalUnreadMessage);
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

    public void handleChatMessage(WebSocketSession session, JsonNode data) {
        ChatMessageReqDto chatMessageReqDto = objectMapper.convertValue(data, ChatMessageReqDto.class);
        System.out.println(chatMessageReqDto);

        ChatMessage chatMessage = saveMessage(chatMessageReqDto);
        ChatMessageResDto chatMessageResDto = convertToChatMessageResDto(chatMessage);
        sendMessage(chatMessageReqDto.getChatId(), chatMessageResDto, session);
    }

    private ChatMessageResDto convertToChatMessageResDto(ChatMessage chatMessage) {
        return ChatMessageResDto.builder()
                .message(chatMessage.getMessage())
                .senderId(chatMessage.getMember().getId())
                .sendAt(TimeUtil.getTimeAgo(chatMessage.getCreatedAt()))
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
    public void sendMessage(Long chatId, ChatMessageResDto message, WebSocketSession session) {
        ChatSocketDto room = findOrMakeChatRoomById(chatId, session);
        System.out.println(room.getSessions());

        if (room != null) {
            for (WebSocketSession se : room.getSessions()) {
                try {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("content", message.getMessage());
                    payload.put("senderId", message.getSenderId());
                    payload.put("sendAt", message.getSendAt());
                    payload.put("isRead", message.getIsRead());

                    Map<String, Object> wrapper = new HashMap<>();
                    wrapper.put("type", "chat_message");
                    wrapper.put("data", payload);

                    System.out.println(payload);
                    se.sendMessage(new TextMessage(objectMapper.writeValueAsString(wrapper)));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    public void broadcastSoldEvent(WebSocketSession session, JsonNode data) {
        Long chatId = data.get("chatId").asLong();
        ChatSocketDto room = findOrMakeChatRoomById(chatId, session);
        if (room != null) {
            for (WebSocketSession se : room.getSessions()) {
                try {
                    se.sendMessage(new TextMessage("{\"type\":\"sold\"}"));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private ChatRoomPageResDto<ChatMessageResDto> convertToChatRoomResDto(ChatRoom chatRoom, Page<ChatMessageResDto> chatMessages) {
        String thumbnailImage = chatRoom.getProduct().getImages().stream()
                .filter(ProductImage::getIsThumbnail)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(null);

        ChatRoomPageResDto<ChatMessageResDto> chatRoomPageResDto = new ChatRoomPageResDto<>(chatMessages);

        chatRoomPageResDto.setSellerId(chatRoom.getSeller().getId());
        chatRoomPageResDto.setSellerNickname(chatRoom.getSeller().getNickname());
        chatRoomPageResDto.setBuyerId(chatRoom.getBuyer().getId());
        chatRoomPageResDto.setBuyerNickname(chatRoom.getBuyer().getNickname());
        chatRoomPageResDto.setSellerProfileImage(chatRoom.getSeller().getProfileImageUrl());
        chatRoomPageResDto.setSellerPhoneNumber(chatRoom.getSeller().getMobileNumber());
        chatRoomPageResDto.setProductId(chatRoom.getProduct().getId());
        chatRoomPageResDto.setProductTitle(chatRoom.getProduct().getTitle());
        chatRoomPageResDto.setProductImage(thumbnailImage);
        chatRoomPageResDto.setProductStatus(chatRoom.getProduct().getStatus().toString());
        chatRoomPageResDto.setProductPrice(chatRoom.getProduct().getCost().toString());
        chatRoomPageResDto.setIsActive(!chatRoom.getIsSellerOut() && !chatRoom.getIsBuyerOut());

        return chatRoomPageResDto;
    }

    public void removeFromChatRoomMap(Long memberId, WebSocketSession session) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByMemberId(memberId);

        for (ChatRoom chatRoom : chatRooms) {
            chatRoomMap.get(chatRoom.getId()).getSessions().remove(session);
        }
    }
}
