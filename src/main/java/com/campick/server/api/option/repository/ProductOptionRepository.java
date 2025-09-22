package com.campick.server.api.option.repository;

import com.campick.server.api.option.entity.CarOption;
import com.campick.server.api.option.entity.ProductOption;
import com.campick.server.api.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findAllByProduct(Product product);

    @Query("""
        SELECT po FROM ProductOption po
        JOIN FETCH po.carOption
        WHERE po.product = :product
    """)
    List<ProductOption> findAllByProductWithOption(@Param("product") Product product);

}
