package com.campick.server.api.model.entity;

import com.campick.server.api.type.entity.Type;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "model")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "market_name")
    private String marketName;
}
