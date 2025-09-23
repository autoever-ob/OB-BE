package com.campick.server.api.chat.entity;

import com.campick.server.api.member.entity.Member;
import com.campick.server.api.product.entity.Product;
import com.campick.server.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Member seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Member buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "is_reviewed")
    @Builder.Default
    private Boolean isReviewed = false;

    @Column(name = "is_sold")
    @Builder.Default
    private Boolean isSold = false;

    @Column(name = "is_seller_out")
    @Builder.Default
    private Boolean isSellerOut = false;

    @Column(name = "is_buyer_out")
    @Builder.Default
    private Boolean isBuyerOut = false;
}
