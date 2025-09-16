package com.campick.server.api.dealer.entity;

import com.campick.server.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dealer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Dealer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dealer_id")
    private Long id;

    @Column(name = "business_no", nullable = false)
    private String businessNo;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user;
}
