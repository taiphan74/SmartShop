package com.ptithcm.smartshop.product.controller;

import com.ptithcm.smartshop.product.dto.CategoryDTO;
import com.ptithcm.smartshop.product.dto.ProductDetailDTO;
import com.ptithcm.smartshop.product.dto.ProductListDTO;
import com.ptithcm.smartshop.product.service.CategoryService;
import com.ptithcm.smartshop.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductWebController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductWebController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String getHome(@RequestParam(value = "category", required = false) String categorySlug, Model model) {
        if (categorySlug == null || categorySlug.isBlank()) {
            List<ProductListDTO> products = productService.findAllProducts();
            model.addAttribute("products", products);
            model.addAttribute("activeCategory", null);
            model.addAttribute("childCategories", List.of());
            model.addAttribute("selectedCategorySlug", null);
            return "product/home";
        }

        Optional<CategoryDTO> categoryOpt = categoryService.findBySlug(categorySlug);
        if (categoryOpt.isEmpty()) {
            return "error/404";
        }

        CategoryDTO activeCategory = categoryOpt.get();
        List<ProductListDTO> products = productService.findPublicProductsByCategorySlug(categorySlug);
        List<CategoryDTO> childCategories = categoryService.findChildrenBySlug(categorySlug);

        model.addAttribute("products", products);
        model.addAttribute("activeCategory", activeCategory);
        model.addAttribute("childCategories", childCategories);
        model.addAttribute("selectedCategorySlug", categorySlug);
        return "product/home";
    }

    @GetMapping("/{slug}")
    public String getProductDetail(@PathVariable("slug") String slug, Model model) {
        Optional<ProductDetailDTO> productOpt = productService.findBySlug(slug);

        if (productOpt.isEmpty()) {
            return "error/404";
        }

        model.addAttribute("product", productOpt.get());
        return "product/detail";
    }
}
