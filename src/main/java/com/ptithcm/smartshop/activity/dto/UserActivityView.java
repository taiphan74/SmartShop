package com.ptithcm.smartshop.activity.dto;

import java.util.List;

public record UserActivityView(
        List<ProductActivityItem> viewedProducts,
        List<ShopActivityItem> viewedShops,
        List<ProductActivityItem> wishlistProducts,
        List<ShopActivityItem> wishlistShops
) {
}
