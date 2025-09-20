package com.campick.server.api.product.entity;

import com.campick.server.api.car.entity.Car;
import com.campick.server.api.favorite.entity.Favorite;
import com.campick.server.api.member.entity.Member;
import com.campick.server.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "product")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Member seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "generation", nullable = false)
    private Integer generation;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Favorite> likes;

    @Column(name = "like_count")
    private Integer likeCount = 0;

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
