package com.campick.server.api.transaction.entity;

import com.campick.server.api.member.entity.Member;
import com.campick.server.api.product.entity.Product;
import com.campick.server.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name="transaction")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Transaction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne
    @JoinColumn( name = "buyer_id")
    private Member buyer;

    @ManyToOne
    @JoinColumn( name = "seller_id")
    private Member seller;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "traded_at")
    private LocalDateTime tradedAt;

    @Column(name = "type")
    private TransactionType type;
}
