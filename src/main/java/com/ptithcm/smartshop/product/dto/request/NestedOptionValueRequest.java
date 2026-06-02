package com.ptithcm.smartshop.product.dto.request;

import jakarta.validation.constraints.NotBlank;

public class NestedOptionValueRequest {

    @NotBlank(message = "Giá trị option không được để trống")
    private String value;

    private Integer sortOrder;

    public NestedOptionValueRequest() {}

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
