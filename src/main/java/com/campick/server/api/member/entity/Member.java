package com.campick.server.api.member.entity;

import com.campick.server.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

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
    private String profileImage;

    @Column(name = "description")
    private String description;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

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
    public void updateProfileImage(String profileImage) { this.profileImage = profileImage;}
}
