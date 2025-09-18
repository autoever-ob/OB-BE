package com.campick.server.api.dealership.entity;

import com.campick.server.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dealership")
@Builder
@AllArgsConstructor
public class DealerShip extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dealership_id")
    private Long id;
    private String name;
    private String address;
    private String registrationNumber;
}
