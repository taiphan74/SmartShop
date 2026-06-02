package com.ptithcm.smartshop.product.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

public class ProductRequest {
    
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;
    
    private String description;
    
    private Boolean status;
    
    @NotBlank(message = "Mã danh mục không được để trống")
    private String categoryId;

    private String shopId;

    private List<String> imageUrls = new ArrayList<>();

    private String mainImageUrl;

    @Valid
    private List<NestedOptionRequest> options = new ArrayList<>();

    @Valid
    private List<NestedVariantRequest> variants = new ArrayList<>();

    public ProductRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public List<NestedOptionRequest> getOptions() { return options; }
    public void setOptions(List<NestedOptionRequest> options) { this.options = options; }

    public List<NestedVariantRequest> getVariants() { return variants; }
    public void setVariants(List<NestedVariantRequest> variants) { this.variants = variants; }
}
