package com.ptithcm.smartshop.activity.service.impl;

import com.ptithcm.smartshop.activity.dto.ProductActivityItem;
import com.ptithcm.smartshop.activity.dto.ShopActivityItem;
import com.ptithcm.smartshop.activity.dto.UserActivityView;
import com.ptithcm.smartshop.activity.entity.UserViewedProduct;
import com.ptithcm.smartshop.activity.entity.UserViewedShop;
import com.ptithcm.smartshop.activity.entity.UserWishlistProduct;
import com.ptithcm.smartshop.activity.entity.UserWishlistShop;
import com.ptithcm.smartshop.activity.repository.UserViewedProductRepository;
import com.ptithcm.smartshop.activity.repository.UserViewedShopRepository;
import com.ptithcm.smartshop.activity.repository.UserWishlistProductRepository;
import com.ptithcm.smartshop.activity.repository.UserWishlistShopRepository;
import com.ptithcm.smartshop.activity.service.UserActivityService;
import com.ptithcm.smartshop.product.entity.Product;
import com.ptithcm.smartshop.product.entity.ProductImage;
import com.ptithcm.smartshop.product.entity.ProductVariant;
import com.ptithcm.smartshop.product.repository.ProductRepository;
import com.ptithcm.smartshop.shared.exception.ResourceNotFoundException;
import com.ptithcm.smartshop.shop.entity.Shop;
import com.ptithcm.smartshop.shop.repository.ShopRepository;
import com.ptithcm.smartshop.user.entity.User;
import com.ptithcm.smartshop.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserActivityServiceImpl implements UserActivityService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final UserViewedProductRepository viewedProductRepository;
    private final UserViewedShopRepository viewedShopRepository;
    private final UserWishlistProductRepository wishlistProductRepository;
    private final UserWishlistShopRepository wishlistShopRepository;

    public UserActivityServiceImpl(UserRepository userRepository,
            ProductRepository productRepository,
            ShopRepository shopRepository,
            UserViewedProductRepository viewedProductRepository,
            UserViewedShopRepository viewedShopRepository,
            UserWishlistProductRepository wishlistProductRepository,
            UserWishlistShopRepository wishlistShopRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
        this.viewedProductRepository = viewedProductRepository;
        this.viewedShopRepository = viewedShopRepository;
        this.wishlistProductRepository = wishlistProductRepository;
        this.wishlistShopRepository = wishlistShopRepository;
    }

    @Override
    public void recordProductView(UUID userId, UUID productId) {
        viewedProductRepository.findByUserIdAndProductId(userId, productId)
                .ifPresentOrElse(existing -> {
                    existing.setViewedAt(Instant.now());
                    viewedProductRepository.save(existing);
                }, () -> {
                    UserViewedProduct viewedProduct = new UserViewedProduct();
                    viewedProduct.setUser(getUserReference(userId));
                    viewedProduct.setProduct(getProductReference(productId));
                    viewedProduct.setViewedAt(Instant.now());
                    viewedProductRepository.save(viewedProduct);
                });
    }

    @Override
    public void recordShopView(UUID userId, UUID shopId) {
        viewedShopRepository.findByUserIdAndShopId(userId, shopId)
                .ifPresentOrElse(existing -> {
                    existing.setViewedAt(Instant.now());
                    viewedShopRepository.save(existing);
                }, () -> {
                    UserViewedShop viewedShop = new UserViewedShop();
                    viewedShop.setUser(getUserReference(userId));
                    viewedShop.setShop(getShopReference(shopId));
                    viewedShop.setViewedAt(Instant.now());
                    viewedShopRepository.save(viewedShop);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductWishlisted(UUID userId, UUID productId) {
        return wishlistProductRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isShopWishlisted(UUID userId, UUID shopId) {
        return wishlistShopRepository.existsByUserIdAndShopId(userId, shopId);
    }

    @Override
    public boolean toggleProductWishlist(UUID userId, UUID productId) {
        return wishlistProductRepository.findByUserIdAndProductId(userId, productId)
                .map(existing -> {
                    wishlistProductRepository.delete(existing);
                    return false;
                })
                .orElseGet(() -> {
                    UserWishlistProduct wishlistProduct = new UserWishlistProduct();
                    wishlistProduct.setUser(getUserReference(userId));
                    wishlistProduct.setProduct(getProductReference(productId));
                    wishlistProductRepository.save(wishlistProduct);
                    return true;
                });
    }

    @Override
    public boolean toggleShopWishlist(UUID userId, UUID shopId) {
        return wishlistShopRepository.findByUserIdAndShopId(userId, shopId)
                .map(existing -> {
                    wishlistShopRepository.delete(existing);
                    return false;
                })
                .orElseGet(() -> {
                    UserWishlistShop wishlistShop = new UserWishlistShop();
                    wishlistShop.setUser(getUserReference(userId));
                    wishlistShop.setShop(getShopReference(shopId));
                    wishlistShopRepository.save(wishlistShop);
                    return true;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public UserActivityView getActivityView(UUID userId, int limit) {
        PageRequest page = PageRequest.of(0, Math.max(1, limit));
        return new UserActivityView(
                viewedProductRepository.findByUserIdOrderByViewedAtDesc(userId, page).stream()
                        .map(item -> toProductItem(item.getProduct(), item.getViewedAt()))
                        .toList(),
                viewedShopRepository.findByUserIdOrderByViewedAtDesc(userId, page).stream()
                        .map(item -> toShopItem(item.getShop(), item.getViewedAt()))
                        .toList(),
                wishlistProductRepository.findByUserIdOrderByCreatedAtDesc(userId, page).stream()
                        .map(item -> toProductItem(item.getProduct(), item.getCreatedAt()))
                        .toList(),
                wishlistShopRepository.findByUserIdOrderByCreatedAtDesc(userId, page).stream()
                        .map(item -> toShopItem(item.getShop(), item.getCreatedAt()))
                        .toList());
    }

    private User getUserReference(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId.toString());
        }
        return userRepository.getReferenceById(userId);
    }

    private Product getProductReference(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId.toString());
        }
        return productRepository.getReferenceById(productId);
    }

    private Shop getShopReference(UUID shopId) {
        if (!shopRepository.existsById(shopId)) {
            throw new ResourceNotFoundException("Shop", shopId.toString());
        }
        return shopRepository.getReferenceById(shopId);
    }

    private ProductActivityItem toProductItem(Product product, Instant activityAt) {
        String thumbnailUrl = product.getImages().stream()
                .filter(ProductImage::getIsMain)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElseGet(() -> product.getImages().stream()
                        .findFirst()
                        .map(ProductImage::getImageUrl)
                        .orElse(null));
        BigDecimal price = product.getVariants().stream()
                .map(ProductVariant::getPrice)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
        return new ProductActivityItem(
                Objects.toString(product.getId(), null),
                product.getName(),
                product.getSlug(),
                thumbnailUrl,
                price,
                categoryName,
                activityAt);
    }

    private ShopActivityItem toShopItem(Shop shop, Instant activityAt) {
        return new ShopActivityItem(
                Objects.toString(shop.getId(), null),
                shop.getName(),
                shop.getSlug(),
                shop.getLogoUrl(),
                shop.getDescription(),
                activityAt);
    }
}
