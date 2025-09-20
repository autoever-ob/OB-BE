package com.campick.server.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ReviewListPageDto {
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
    private boolean isLast;
    private List<ReviewResponseDto> content;
}
