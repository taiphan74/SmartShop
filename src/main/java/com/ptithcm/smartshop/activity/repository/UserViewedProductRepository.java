package com.ptithcm.smartshop.activity.repository;

import com.ptithcm.smartshop.activity.entity.UserViewedProduct;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserViewedProductRepository extends JpaRepository<UserViewedProduct, UUID> {

    Optional<UserViewedProduct> findByUserIdAndProductId(UUID userId, UUID productId);

    @Query("""
            SELECT viewedProduct
            FROM UserViewedProduct viewedProduct
            JOIN FETCH viewedProduct.product product
            JOIN FETCH product.category
            JOIN FETCH product.shop
            WHERE viewedProduct.user.id = :userId
            ORDER BY viewedProduct.viewedAt DESC
            """)
    List<UserViewedProduct> findByUserIdOrderByViewedAtDesc(@Param("userId") UUID userId, Pageable pageable);
}
