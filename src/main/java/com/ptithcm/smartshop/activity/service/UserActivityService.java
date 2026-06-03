package com.ptithcm.smartshop.activity.service;

import com.ptithcm.smartshop.activity.dto.UserActivityView;
import java.util.UUID;

public interface UserActivityService {

    void recordProductView(UUID userId, UUID productId);

    void recordShopView(UUID userId, UUID shopId);

    boolean isProductWishlisted(UUID userId, UUID productId);

    boolean isShopWishlisted(UUID userId, UUID shopId);

    boolean toggleProductWishlist(UUID userId, UUID productId);

    boolean toggleShopWishlist(UUID userId, UUID shopId);

    UserActivityView getActivityView(UUID userId, int limit);
}
