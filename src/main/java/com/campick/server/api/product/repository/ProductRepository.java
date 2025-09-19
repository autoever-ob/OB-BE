package com.campick.server.api.product.repository;

import com.campick.server.api.member.entity.Member;
import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //             엔진
    //              🔻
    // 종류 > 모델 > 차 > 제품 > 유저
    // type > model > car > product > member
    // 이미지는 OneToMany를 건다 >> 어차피 많이 조회
    @Query("SELECT p FROM Product p " +
            "JOIN fetch p.seller m " +
            "JOIN fetch p.car c " +
            "JOIN fetch c.model mo " +
            "JOIN fetch c.engine e " +
            "join fetch mo.type t " +
            // images 가 없는 경우를 포함하기 위해서 LEFT JOIN
            "left join fetch p.images i " +
            // 상태가 '판매'로 되어있는 것을 찾아냄
            "where m.id = :memberId AND ( p.status = 'AVAILABLE' OR p.status = 'RESERVED')")
    // 여기서 @Param은 JPQL 쿼리문안에서 : 로 시작하는 부분에 붙여줌
    // 이름 기반 파라미터라고 부름
    // Spring Data JPA는 메서드가 호출될 때 전달된 memberId라는 값을 찾아서 쿼리문에 붙여줌
    List<Product> findProductByMemberIdWithDetails(@Param("memberId") Long memberId);

    List<Product> findProductsBySellerAndStatus(Member buyer, ProductStatus productStatus);
}
