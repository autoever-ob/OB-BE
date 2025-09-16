package com.campick.server.api.favorite.service;

import com.campick.server.api.favorite.entity.Favorite;
import com.campick.server.api.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    public List<Favorite> findAll() {
        return favoriteRepository.findAll();
    }
}
