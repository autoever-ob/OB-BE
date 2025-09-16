package com.campick.server.api.engine.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "engine")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Engine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "engine_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "feul_type", nullable = false)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transmission", nullable = false)
    private Transmission transmission;

    @Column(name = "horse_power")
    private Integer horsePower;

    @Column(name = "fuel_efficiency")
    private Double fuelEfficiency;
}
