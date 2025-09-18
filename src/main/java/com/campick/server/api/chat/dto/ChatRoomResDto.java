package com.campick.server.api.chat.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatRoomResDto {
    private Long sellerId;
    private Long buyerId;
    private String sellerNickname;
    private String buyerNickname;
    private String sellerProfileImage;
    private String sellerPhoneNumber;
    private Long productId;
    private String productTitle;
    private String productStatus;
    private String productPrice;
    private Boolean isActive;
    private List<ChatMessageResDto> chatData;
}
