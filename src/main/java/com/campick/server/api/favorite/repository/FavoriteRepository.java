package com.campick.server.api.favorite.repository;

import com.campick.server.api.favorite.entity.Favorite;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Favorite findByMemberAndProduct(Member member, Product product);
}
