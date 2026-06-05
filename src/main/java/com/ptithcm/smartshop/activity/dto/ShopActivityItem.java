package com.ptithcm.smartshop.activity.dto;

import java.time.Instant;

public record ShopActivityItem(
        String id,
        String name,
        String slug,
        String logoUrl,
        String description,
        Instant activityAt
) {
}
