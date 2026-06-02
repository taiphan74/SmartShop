package com.ptithcm.smartshop.product.service.impl;

import com.ptithcm.smartshop.product.dto.PageResponse;
import com.ptithcm.smartshop.product.dto.ProductDetailDTO;
import com.ptithcm.smartshop.product.dto.ProductListDTO;
import com.ptithcm.smartshop.product.dto.request.NestedOptionRequest;
import com.ptithcm.smartshop.product.dto.request.NestedOptionValueRequest;
import com.ptithcm.smartshop.product.dto.request.NestedVariantRequest;
import com.ptithcm.smartshop.product.dto.request.ProductRequest;
import com.ptithcm.smartshop.product.entity.Category;
import com.ptithcm.smartshop.product.entity.Product;
import com.ptithcm.smartshop.product.entity.ProductImage;
import com.ptithcm.smartshop.product.entity.ProductOption;
import com.ptithcm.smartshop.product.entity.ProductOptionValue;
import com.ptithcm.smartshop.product.entity.ProductVariant;
import com.ptithcm.smartshop.product.mapper.ProductMapper;
import com.ptithcm.smartshop.product.repository.CategoryRepository;
import com.ptithcm.smartshop.product.repository.ProductOptionRepository;
import com.ptithcm.smartshop.product.repository.ProductOptionValueRepository;
import com.ptithcm.smartshop.product.repository.ProductProjection;
import com.ptithcm.smartshop.product.repository.ProductRepository;
import com.ptithcm.smartshop.product.repository.ProductVariantRepository;
import com.ptithcm.smartshop.product.service.ProductService;
import com.ptithcm.smartshop.shared.exception.ResourceNotFoundException;
import com.ptithcm.smartshop.shared.util.SlugUtil;
import com.ptithcm.smartshop.shop.entity.Shop;
import com.ptithcm.smartshop.shop.repository.ShopRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final ProductMapper productMapper;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductVariantRepository productVariantRepository;

    public ProductServiceImpl(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ShopRepository shopRepository,
            ProductMapper productMapper,
            ProductOptionRepository productOptionRepository,
            ProductOptionValueRepository productOptionValueRepository,
            ProductVariantRepository productVariantRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.shopRepository = shopRepository;
        this.productMapper = productMapper;
        this.productOptionRepository = productOptionRepository;
        this.productOptionValueRepository = productOptionValueRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductListDTO> findAll(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<ProductProjection> projections = productRepository.findAllProjection(pageable);
        return convertToPageResponse(projections);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductListDTO> findAllProducts() {
        List<ProductProjection> projections = productRepository.findPublicProductsFromApprovedShops();
        return productMapper.toProjectionDTOList(projections);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductListDTO> findPublicProductsByCategorySlug(String categorySlug) {
        Category category = categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categorySlug));
        List<ProductProjection> projections = productRepository.findPublicProductsByCategoryPath(category.getPath());
        return productMapper.toProjectionDTOList(projections);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDetailDTO> findById(String id) {
        return productRepository.findById(parseUuid(id, "id")).map(productMapper::toDetailDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDetailDTO> findBySlug(String slug) {
        return productRepository.findBySlug(slug).map(productMapper::toDetailDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductListDTO> findByCategory(String categoryId, int pageNo, int pageSize, String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<ProductProjection> projections = productRepository
                .findByCategoryProjection(parseUuid(categoryId, "categoryId"), pageable);
        return convertToPageResponse(projections);
    }

    private PageResponse<ProductListDTO> convertToPageResponse(Page<ProductProjection> page) {
        List<ProductListDTO> content = productMapper.toProjectionDTOList(page.getContent());
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }

    @Override
    public ProductDetailDTO save(ProductRequest request) {
        String baseSlug = SlugUtil.toSlug(request.getName());
        String slug = generateUniqueSlug(baseSlug);

        Product product = productMapper.toEntity(request);
        product.setSlug(slug);

        Category category = categoryRepository.findById(parseUuid(request.getCategoryId(), "categoryId"))
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        product.setCategory(category);

        if (request.getShopId() != null && !request.getShopId().isBlank()) {
            Shop shop = shopRepository.findById(parseUuid(request.getShopId(), "shopId"))
                    .orElseThrow(() -> new ResourceNotFoundException("Shop", request.getShopId()));
            product.setShop(shop);
        }

        // === MAP IMAGES ===
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            String mainUrl = request.getMainImageUrl();
            int order = 0;
            for (String url : request.getImageUrls()) {
                ProductImage img = new ProductImage();
                img.setImageUrl(url);
                img.setIsMain(mainUrl != null && url.equals(mainUrl));
                img.setSortOrder(order++);
                img.setProduct(product);
                product.getImages().add(img);
            }
        }
        // === END MAP IMAGES ===

        // Save product FIRST to get ID for child entities
        product = productRepository.save(product);

        saveOptionsAndVariants(product, request);

        return productMapper.toDetailDTO(product);
    }

    @Override
    public ProductDetailDTO update(String id, ProductRequest request) {
        UUID productId = parseUuid(id, "id");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (!java.util.Objects.equals(product.getName(), request.getName())) {
            String baseSlug = SlugUtil.toSlug(request.getName());
            if (!baseSlug.equals(product.getSlug())) {
                String slug = generateUniqueSlug(baseSlug);
                product.setSlug(slug);
            }
        }

        productMapper.updateEntity(request, product);

        Category category = categoryRepository.findById(parseUuid(request.getCategoryId(), "categoryId"))
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        product.setCategory(category);

        // === MAP IMAGES ===
        product.getImages().clear(); // Xóa ảnh cũ (orphanRemoval tự DELETE DB)
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            String mainUrl = request.getMainImageUrl();
            int order = 0;
            for (String url : request.getImageUrls()) {
                ProductImage img = new ProductImage();
                img.setImageUrl(url);
                img.setIsMain(mainUrl != null && url.equals(mainUrl));
                img.setSortOrder(order++);
                img.setProduct(product);
                product.getImages().add(img);
            }
        }
        // === END MAP IMAGES ===

        // === UPDATE OPTIONS & VARIANTS ===
        product.getOptions().clear();
        product.getVariants().clear();
        product = productRepository.saveAndFlush(product);

        saveOptionsAndVariants(product, request);

        return productMapper.toDetailDTO(product);
    }

    @Override
    public void deleteById(String id) {
        UUID productId = parseUuid(id, "id");
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", id);
        }
        productRepository.deleteById(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductListDTO> findProductsByShop(UUID shopId, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<ProductProjection> projections = productRepository.findByShopIdProjection(shopId, pageable);
        return convertToPageResponse(projections);
    }

    @Override
    public void deleteProductByShop(UUID productId, UUID shopId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId.toString()));
        if (!product.getShop().getId().equals(shopId)) {
            throw new IllegalArgumentException("Sản phẩm không thuộc shop này");
        }
        productRepository.deleteById(productId);
    }

    private String generateUniqueSlug(String baseSlug) {
        if (!productRepository.findBySlug(baseSlug).isPresent()) {
            return baseSlug;
        }

        String slug;
        do {
            slug = baseSlug + "-" + SlugUtil.randomSuffix(6);
        } while (productRepository.findBySlug(slug).isPresent());
        return slug;
    }

    private UUID parseUuid(String value, String field) {
        try {
            return UUID.fromString(value);
        } catch (Exception ex) {
            throw new ResourceNotFoundException("Invalid " + field + ": " + value);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductProjection> searchApprovedProducts(String keyword, Pageable pageable) {
        String sanitized = keyword == null ? "" : keyword.trim();
        if (sanitized.isEmpty()) {
            return Page.empty(pageable);
        }
        if (sanitized.length() > 200) {
            sanitized = sanitized.substring(0, 200);
        }
        return productRepository.searchPublicProducts(sanitized, pageable);
    }

    private void saveOptionsAndVariants(Product product, ProductRequest request) {
        List<UUID> flatValueIds = new ArrayList<>();
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            int optionOrder = 0;
            for (NestedOptionRequest optReq : request.getOptions()) {
                ProductOption option = new ProductOption();
                option.setName(optReq.getName());
                option.setSortOrder(optReq.getSortOrder() != null ? optReq.getSortOrder() : optionOrder);
                option.setProduct(product);
                option = productOptionRepository.save(option);

                if (optReq.getValues() != null) {
                    int valOrder = 0;
                    for (NestedOptionValueRequest valReq : optReq.getValues()) {
                        ProductOptionValue value = new ProductOptionValue();
                        value.setValue(valReq.getValue());
                        value.setSortOrder(valReq.getSortOrder() != null ? valReq.getSortOrder() : valOrder++);
                        value.setOption(option);
                        value = productOptionValueRepository.save(value);
                        flatValueIds.add(value.getId());
                    }
                }
                optionOrder++;
            }
        }

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (NestedVariantRequest varReq : request.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setSku(varReq.getSku());
                variant.setPrice(varReq.getPrice());
                variant.setCompareAtPrice(varReq.getCompareAtPrice());
                variant.setStockQuantity(varReq.getStockQuantity() != null ? varReq.getStockQuantity() : 0);
                variant.setStatus(varReq.getStatus() != null ? varReq.getStatus() : true);
                variant.setThumbnailUrl(varReq.getThumbnailUrl());
                variant.setProduct(product);

                List<ProductOptionValue> resolvedValues = new ArrayList<>();
                if (varReq.getOptionValueIndexes() != null) {
                    for (Integer idx : varReq.getOptionValueIndexes()) {
                        if (idx >= 0 && idx < flatValueIds.size()) {
                            ProductOptionValue ov = new ProductOptionValue();
                            ov.setId(flatValueIds.get(idx));
                            resolvedValues.add(ov);
                        } else {
                            log.warn("Invalid optionValueIndex {} for variant SKU {}. Max valid index: {}",
                                idx, varReq.getSku(), flatValueIds.size() - 1);
                        }
                    }
                }
                variant.setOptionValues(resolvedValues);
                productVariantRepository.save(variant);
            }
        }
    }
}
