package com.campick.server.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageUpdateResponseDto {
    private String profileImageUrl;
    private String profileThumbnailUrl;

    public static ProfileImageUpdateResponseDto from(Map<String, String> urls) {
        return ProfileImageUpdateResponseDto.builder()
                .profileImageUrl(urls.get("profileImageUrl"))
                .profileThumbnailUrl(urls.get("profileThumbnailUrl"))
                .build();
    }
}
