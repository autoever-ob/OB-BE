package com.campick.server.api.favorite.controller;

import com.campick.server.api.favorite.entity.Favorite;
import com.campick.server.api.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping
    public List<Favorite> getFavorites() {
        return favoriteService.findAll();
    }
}
