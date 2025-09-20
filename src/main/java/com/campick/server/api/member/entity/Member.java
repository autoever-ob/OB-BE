package com.campick.server.api.member.entity;

import com.campick.server.api.dealer.entity.Dealer;
import com.campick.server.api.review.entity.Review;
import com.campick.server.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "member")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "profile_image")
    private String profileImageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    // 내가 작성한 리뷰들
    @Builder.Default // 객체에 대해서는 Builder을 써주는 게 좋음
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> writtenReviews = new ArrayList<>();

    // 내가 받은 리뷰들
    @Builder.Default
    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> receivedReviews = new ArrayList<>();

    private String refreshToken;
    private LocalDateTime refreshTokenExpiration;

    public void updateRefreshToken(String refreshToken, long expireMs) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiration = LocalDateTime.now().plus(Duration.ofMillis(expireMs));
    }


    //닉네임 변경
    public void updateNickname(String nickname) { this.nickname = nickname; }

    // 비밀번호 변경
    public void updatePassword(String password) { this.password = password; }

    // 프로필 이미지 변경
    public void updateProfileImage(String profileImageUrl) { this.profileImageUrl = profileImageUrl;}

    // 딜러 연관관계 설정 (멤버 -> 딜러)
    public void assignDealer(Dealer dealer) {
        this.dealer = dealer;
    }
}
