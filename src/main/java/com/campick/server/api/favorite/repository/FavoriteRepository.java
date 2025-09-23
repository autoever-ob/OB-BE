package com.campick.server.api.favorite.repository;

import com.campick.server.api.favorite.entity.Favorite;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Favorite findByMemberAndProduct(Member member, Product product);

    Set<Favorite> findByMemberId(Long memberId);

    long countByMemberId(Long memberId);

    @Query(value = "SELECT f FROM Favorite f " +
            "JOIN FETCH f.product p " +
            "JOIN FETCH p.car c " +
            "JOIN FETCH c.engine e " +
            "LEFT JOIN FETCH p.images " +
            "WHERE f.member.id = :memberId",
            // JOINT FETCH로 인한 제대로된 숫자 세기가 안되는 것을 방지
            // Pageable을 보낼 때 전체 요소의 개수를 반환하는데 1개의 글에 3개의 댓글이 있으면
            // 3개로 세지는 문제를 방지하기 위함
            countQuery = "SELECT count(f) FROM Favorite f WHERE f.member.id = :memberId")
    Page<Favorite> findFavoritesByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
