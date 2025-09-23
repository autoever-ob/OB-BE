package com.campick.server.api.dealer.entity;

import com.campick.server.api.dealership.entity.DealerShip;
import com.campick.server.api.member.entity.Member;
import com.campick.server.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dealer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Dealer extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dealer_id")
    private Long id;

    @Column(name = "business_no", nullable = false)
    private String businessNo;

    @Builder.Default
    @Column(name = "rating", nullable = false)
    private Double rating = 0.0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="dealership_id")
    private DealerShip dealerShip;
}
