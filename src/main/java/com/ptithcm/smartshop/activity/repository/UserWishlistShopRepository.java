package com.ptithcm.smartshop.activity.repository;

import com.ptithcm.smartshop.activity.entity.UserWishlistShop;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWishlistShopRepository extends JpaRepository<UserWishlistShop, UUID> {

    boolean existsByUserIdAndShopId(UUID userId, UUID shopId);

    Optional<UserWishlistShop> findByUserIdAndShopId(UUID userId, UUID shopId);

    @EntityGraph(attributePaths = {"shop"})
    List<UserWishlistShop> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
