package com.ptithcm.smartshop.admin.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class AdminCategoryForm {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
    private String slug;
    private String path;
    private Integer level;
    private UUID parentId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
}
