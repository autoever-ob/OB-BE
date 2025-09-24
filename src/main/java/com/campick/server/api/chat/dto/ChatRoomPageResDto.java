package com.campick.server.api.chat.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter @Setter
public class ChatRoomPageResDto<T> {
    private final long totalElements; // 잔체 요소 수
    private final int totalPages; // 전체 페이지 수
    private final int page; // 현재 페이지(0부터 시작)
    private final int size; // 요청한 페이지 사이즈
    private final boolean isLast; // 마지막 페이지 여부

    private Long sellerId;
    private Long buyerId;
    private String sellerNickname;
    private String buyerNickname;
    private String sellerProfileImage;
    private String sellerPhoneNumber;
    private Long productId;
    private String productTitle;
    private String productImage;
    private String productStatus;
    private String productPrice;
    private Boolean isActive;

    private final List<T> chatData;

    public ChatRoomPageResDto(Page<T> page) {
        this.chatData = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.isLast = page.isLast();
    }
}
