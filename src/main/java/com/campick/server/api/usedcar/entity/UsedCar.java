package com.campick.server.api.usedcar.entity;

import com.campick.server.api.car.entity.Car;
import com.campick.server.api.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "used_car")
public class UsedCar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", referencedColumnName = "car_id")
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "user_id")
    private User seller;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "driven_kilo", nullable = false)
    private Integer drivenKilo;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "prduct_image", nullable = false)
    private String prductImage;

    @Column(name = "plate_hash", nullable = false)
    private String plateHash;

    @Column(name = "exterior_color")
    private String exteriorColor;

    @Column(name = "interior_color")
    private String interiorColor;

    @Column(name = "location", nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "options")
    private String options;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public enum Status {
        PENDING, SELLING, SOLD
    }
}
