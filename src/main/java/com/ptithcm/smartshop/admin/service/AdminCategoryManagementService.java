package com.ptithcm.smartshop.admin.service;

import com.ptithcm.smartshop.admin.dto.AdminCategoryForm;
import com.ptithcm.smartshop.product.entity.Category;
import com.ptithcm.smartshop.product.repository.CategoryRepository;
import com.ptithcm.smartshop.product.repository.ProductRepository;
import com.ptithcm.smartshop.shared.util.SlugUtil;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminCategoryManagementService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public AdminCategoryManagementService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<Category> list(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Category get(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
    }

    @Transactional
    public Category create(AdminCategoryForm form) {
        Category category = new Category();
        applyForm(category, form, null);
        return categoryRepository.save(category);
    }

    @Transactional
    public void update(UUID categoryId, AdminCategoryForm form) {
        Category category = get(categoryId);
        applyForm(category, form, categoryId);
        refreshChildPaths(category);
    }

    @Transactional
    public void delete(UUID categoryId) {
        Category category = get(categoryId);
        if (productRepository.countByCategoryId(categoryId) > 0 || categoryRepository.countByParentId(categoryId) > 0) {
            throw new IllegalArgumentException("Danh mục còn sản phẩm hoặc danh mục con");
        }
        categoryRepository.delete(category);
    }

    private void applyForm(Category category, AdminCategoryForm form, UUID currentId) {
        Category parent = form.getParentId() == null ? null : get(form.getParentId());
        if (parent != null && currentId != null && isSelfOrDescendant(parent, currentId)) {
            throw new IllegalArgumentException("Danh mục cha không hợp lệ");
        }

        String slug = createUniqueSlug(form.getName(), currentId);
        category.setName(form.getName().trim());
        category.setParent(parent);
        category.setSlug(slug);
        category.setLevel(parent == null ? 0 : parent.getLevel() + 1);
        category.setPath(parent == null ? slug : parent.getPath() + "/" + slug);
    }

    private String createUniqueSlug(String name, UUID currentId) {
        String baseSlug = SlugUtil.toSlug(name.trim());
        if (baseSlug.isBlank()) {
            baseSlug = "danh-muc";
        }

        String slug = baseSlug;
        int suffix = 2;
        while (currentId == null ? categoryRepository.existsBySlug(slug) : categoryRepository.existsBySlugAndIdNot(slug, currentId)) {
            slug = baseSlug + "-" + suffix++;
        }
        return slug;
    }

    private boolean isSelfOrDescendant(Category candidateParent, UUID categoryId) {
        Category current = candidateParent;
        while (current != null) {
            if (categoryId.equals(current.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private void refreshChildPaths(Category parent) {
        for (Category child : categoryRepository.findByParent(parent)) {
            child.setLevel(parent.getLevel() + 1);
            child.setPath(parent.getPath() + "/" + child.getSlug());
            refreshChildPaths(child);
        }
    }
}
