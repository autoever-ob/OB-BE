package com.campick.server.api.favorite.service;

import com.campick.server.api.favorite.entity.Favorite;
import com.campick.server.api.favorite.repository.FavoriteRepository;
import com.campick.server.api.member.dto.ProductAllSummaryDto;
import com.campick.server.common.dto.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    public PageResponseDto<ProductAllSummaryDto> getFavoriteProducts(Long memberId, Pageable pageable) {
        Page<ProductAllSummaryDto> page = favoriteRepository.findFavoritesByMemberId(memberId, pageable)
                .map(favorite -> ProductAllSummaryDto.from(favorite.getProduct()));
        return new PageResponseDto<>(page);
    }

    public long getFavoriteCount(Long memberId) {
        return favoriteRepository.countByMemberId(memberId);
    }

    public List<Favorite> findAll() {
        return favoriteRepository.findAll();
    }
}
