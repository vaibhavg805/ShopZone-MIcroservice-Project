package com.vaibhav.product_service.serviceImpl;

import com.vaibhav.product_service.dto.ProductRequestDto;
import com.vaibhav.product_service.dto.ProductResponseDto;
import com.vaibhav.product_service.entity.Brand;
import com.vaibhav.product_service.entity.Category;
import com.vaibhav.product_service.entity.Product;
import com.vaibhav.product_service.exception.BrandNotExistException;
import com.vaibhav.product_service.exception.CategoryIdNotExist;
import com.vaibhav.product_service.exception.ProductNotFoundException;
import com.vaibhav.product_service.repository.BrandRepository;
import com.vaibhav.product_service.repository.CategoryRepository;
import com.vaibhav.product_service.repository.ProductRepository;
import com.vaibhav.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    /**
     * @param dto
     * @return
     */
    @Override
    public ProductResponseDto createProduct(ProductRequestDto dto) {
        if (productRepository.existsByName(dto.getName())) {
            throw new DataIntegrityViolationException("Product Name Already Present in Records...");
        }
        if (productRepository.existsBySku(dto.getSku())) {
            throw new DataIntegrityViolationException("Product SKU Already Present in Records...");
        }

        Category category = null;
        if (dto.getCategoryId() != null) {
            category =  categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new CategoryIdNotExist("Category Id Not Exist Exception..."));
        }

        Brand brand = null;
        if (dto.getBrandId() != null) {
            brand = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new BrandNotExistException("Brand Id Not Exist Exception..."));
        }

      Product resProduct =  Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(category)
                .brand(brand)
                .basePrice(dto.getBasePrice())
                .discountPercentage(dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : BigDecimal.ZERO)
                .sku(dto.getSku().trim())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .specifications(dto.getSpecifications())
                .build();

       Product savedProduct = productRepository.save(resProduct);
       return this.toResponseDto(savedProduct);
    }

    private ProductResponseDto toResponseDto(Product product) {
        if (product == null) return null;

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .basePrice(product.getBasePrice())
                .discountPercentage(product.getDiscountPercentage())
                .finalPrice(product.getFinalPrice())
                .sku(product.getSku())
                .isActive(product.getIsActive())
                .specifications(product.getSpecifications())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .build();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        return toResponseDto(product);
    }

    /**
     * @param id
     * @param dto
     * @return
     */
    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryIdNotExist("Category Id Not Exist"));

        Brand brand = null;
        if (dto.getBrandId() != null) {
            brand = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new BrandNotExistException("Brand Id Not Exist"));
        }

        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription());
        product.setCategory(category);
        product.setBrand(brand);
        product.setBasePrice(dto.getBasePrice());
        product.setDiscountPercentage(
                dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : BigDecimal.ZERO
        );
        product.setSku(dto.getSku().trim());
        product.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        product.setSpecifications(dto.getSpecifications());

        Product updated = productRepository.save(product);

        return toResponseDto(updated);
    }

    /**
     * @param id
     */
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product.setIsActive(false);

        productRepository.save(product);
    }

    /**
     * @param categoryId
     * @param brandId
     * @param minPrice
     * @param maxPrice
     * @param pageable
     * @return
     */
    @Override
    public Page<ProductResponseDto> searchProducts(Long categoryId, Long brandId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId, pageable);
        } else if (brandId != null) {
            products = productRepository.findByBrandId(brandId, pageable);
        } else if (minPrice != null && maxPrice != null) {
            products = productRepository.findByBasePriceBetween(minPrice, maxPrice, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }
        return products.map(this::toResponseDto);
    }

    /**
     * @param categoryId
     * @return
     */
    @Override
    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        return List.of();
    }

    /**
     * @param brandId
     * @return
     */
    @Override
    public List<ProductResponseDto> getProductsByBrand(Long brandId) {
        return List.of();
    }

    /**
     * @param limit
     * @return
     */
    @Override
    public List<ProductResponseDto> getFeaturedProducts(int limit) {
        return List.of();
    }

    /**
     * @param limit
     * @return
     */
    @Override
    public List<ProductResponseDto> getTrendingProducts(int limit) {
        return List.of();
    }

    /**
     * @return
     */
    @Override
    public List<ProductResponseDto> getActiveProducts() {
        return List.of();
    }

    /**
     * @return
     */
    @Override
    public List<ProductResponseDto> getAllProducts() {
        return List.of();
    }
}
