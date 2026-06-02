package com.ptithcm.smartshop.product.service;

import com.ptithcm.smartshop.product.mapper.ProductMapper;
import com.ptithcm.smartshop.product.repository.CategoryRepository;
import com.ptithcm.smartshop.product.repository.ProductProjection;
import com.ptithcm.smartshop.product.repository.ProductRepository;
import com.ptithcm.smartshop.product.service.impl.ProductServiceImpl;
import com.ptithcm.smartshop.shop.repository.ShopRepository;
import com.ptithcm.smartshop.product.repository.ProductOptionRepository;
import com.ptithcm.smartshop.product.repository.ProductOptionValueRepository;
import com.ptithcm.smartshop.product.repository.ProductVariantRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductMarketplaceVisibilityTest {

    @Test
    void findAllProductsUsesApprovedShopQuery() {
        ProductRepository productRepository = mock(ProductRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        ShopRepository shopRepository = mock(ShopRepository.class);
        ProductMapper productMapper = mock(ProductMapper.class);
        ProductOptionRepository optionRepo = mock(ProductOptionRepository.class);
        ProductOptionValueRepository valueRepo = mock(ProductOptionValueRepository.class);
        ProductVariantRepository variantRepo = mock(ProductVariantRepository.class);
        ProductServiceImpl service = new ProductServiceImpl(productRepository, categoryRepository, shopRepository, productMapper, optionRepo, valueRepo, variantRepo);
        ProductProjection projection = mock(ProductProjection.class);
        when(productRepository.findPublicProductsFromApprovedShops()).thenReturn(List.of(projection));
        when(productMapper.toProjectionDTOList(List.of(projection))).thenReturn(List.of());

        service.findAllProducts();

        verify(productRepository).findPublicProductsFromApprovedShops();
    }
}
