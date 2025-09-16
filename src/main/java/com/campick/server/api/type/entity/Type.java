package com.campick.server.api.type.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_name", nullable = false)
    private VehicleTypeName typeName;
}
