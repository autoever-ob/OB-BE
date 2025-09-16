package com.campick.server.api.product.entity;

import com.campick.server.api.car.entity.Car;
import com.campick.server.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "mileage", nullable = false)
    private Integer mileage;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "plate_hash", nullable = false)
    private String plateHash;

    @Column(name = "exterior_color")
    private String exteriorColor;

    @Column(name = "interiror_color")
    private String interiorColor; // column typo kept as interiror_color

    @Column(name = "location", nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ProductType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
