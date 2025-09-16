package com.campick.server.api.product.repository;

import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    ProductImage findByProductAndIsThumbnailTrue(Product product);
    List<ProductImage> findAllByProduct(Product product);
}
