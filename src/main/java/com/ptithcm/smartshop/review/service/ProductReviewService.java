package com.ptithcm.smartshop.review.service;

import com.ptithcm.smartshop.product.dto.PageResponse;
import com.ptithcm.smartshop.review.dto.ReviewRequest;
import com.ptithcm.smartshop.review.dto.ReviewResponse;

import java.util.List;
import java.util.UUID;

public interface ProductReviewService {
    ReviewResponse createReview(UUID productId, UUID userId, ReviewRequest request);
    PageResponse<ReviewResponse> getProductReviews(UUID productId, int page, int size);
    List<UUID> getEligibleOrdersForReview(UUID productId, UUID userId);
}
