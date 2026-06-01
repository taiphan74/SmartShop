package com.ptithcm.smartshop.product.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class NestedOptionRequest {

    @NotBlank(message = "Tên option không được để trống")
    private String name;

    private Integer sortOrder;

    @Valid
    private List<NestedOptionValueRequest> values = new ArrayList<>();

    public NestedOptionRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public List<NestedOptionValueRequest> getValues() { return values; }
    public void setValues(List<NestedOptionValueRequest> values) { this.values = values; }
}
