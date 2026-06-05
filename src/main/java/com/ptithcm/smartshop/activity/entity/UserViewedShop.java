package com.ptithcm.smartshop.activity.entity;

import com.ptithcm.smartshop.shared.entity.BaseUuidEntity;
import com.ptithcm.smartshop.shop.entity.Shop;
import com.ptithcm.smartshop.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
        name = "user_viewed_shops",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_viewed_shops_user_shop", columnNames = {"user_id", "shop_id"}),
        indexes = {
                @Index(name = "idx_user_viewed_shops_user_viewed", columnList = "user_id, viewed_at"),
                @Index(name = "idx_user_viewed_shops_shop", columnList = "shop_id")
        })
public class UserViewedShop extends BaseUuidEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "viewed_at", nullable = false)
    private Instant viewedAt;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(Instant viewedAt) {
        this.viewedAt = viewedAt;
    }
}
