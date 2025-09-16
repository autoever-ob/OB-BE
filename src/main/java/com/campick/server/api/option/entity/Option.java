package com.campick.server.api.option.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}
