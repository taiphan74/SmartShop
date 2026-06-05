package com.ptithcm.smartshop.activity.repository;

import com.ptithcm.smartshop.activity.entity.UserWishlistProduct;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserWishlistProductRepository extends JpaRepository<UserWishlistProduct, UUID> {

    boolean existsByUserIdAndProductId(UUID userId, UUID productId);

    Optional<UserWishlistProduct> findByUserIdAndProductId(UUID userId, UUID productId);

    @Query("""
            SELECT wishlistProduct
            FROM UserWishlistProduct wishlistProduct
            JOIN FETCH wishlistProduct.product product
            JOIN FETCH product.category
            JOIN FETCH product.shop
            WHERE wishlistProduct.user.id = :userId
            ORDER BY wishlistProduct.createdAt DESC
            """)
    List<UserWishlistProduct> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId, Pageable pageable);
}
