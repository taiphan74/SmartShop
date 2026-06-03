package com.ptithcm.smartshop.activity.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductActivityItem(
        String id,
        String name,
        String slug,
        String thumbnailUrl,
        BigDecimal price,
        String categoryName,
        Instant activityAt
) {
}
