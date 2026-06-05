package com.ptithcm.smartshop.activity.repository;

import com.ptithcm.smartshop.activity.entity.UserViewedShop;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserViewedShopRepository extends JpaRepository<UserViewedShop, UUID> {

    Optional<UserViewedShop> findByUserIdAndShopId(UUID userId, UUID shopId);

    @EntityGraph(attributePaths = {"shop"})
    List<UserViewedShop> findByUserIdOrderByViewedAtDesc(UUID userId, Pageable pageable);
}
