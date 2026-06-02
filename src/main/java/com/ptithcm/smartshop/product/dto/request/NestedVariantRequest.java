package com.ptithcm.smartshop.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NestedVariantRequest {

    @NotBlank(message = "SKU không được để trống")
    private String sku;

    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private Integer stockQuantity;
    private Boolean status;
    private String thumbnailUrl;

    // Index-based linking: references index in flattened option values list
    private List<Integer> optionValueIndexes = new ArrayList<>();

    public NestedVariantRequest() {}

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getCompareAtPrice() { return compareAtPrice; }
    public void setCompareAtPrice(BigDecimal compareAtPrice) { this.compareAtPrice = compareAtPrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public List<Integer> getOptionValueIndexes() { return optionValueIndexes; }
    public void setOptionValueIndexes(List<Integer> optionValueIndexes) { this.optionValueIndexes = optionValueIndexes; }
}
