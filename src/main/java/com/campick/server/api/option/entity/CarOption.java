package com.campick.server.api.option.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "car_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CarOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}
