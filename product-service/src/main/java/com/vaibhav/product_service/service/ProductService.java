package com.vaibhav.product_service.service;

import com.vaibhav.product_service.dto.ProductRequestDto;
import com.vaibhav.product_service.dto.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

        ProductResponseDto createProduct(ProductRequestDto dto);

        ProductResponseDto getProductById(Long id);

        ProductResponseDto updateProduct(Long id, ProductRequestDto dto);

        void deleteProduct(Long id);

        Page<ProductResponseDto> searchProducts(
                Long categoryId,
                Long brandId,
                BigDecimal minPrice,
                BigDecimal maxPrice,
                Pageable pageable
        );

        List<ProductResponseDto> getProductsByCategory(Long categoryId);

        List<ProductResponseDto> getProductsByBrand(Long brandId);

        List<ProductResponseDto> getFeaturedProducts(int limit);

        List<ProductResponseDto> getTrendingProducts(int limit);

        List<ProductResponseDto> getActiveProducts();

        List<ProductResponseDto> getAllProducts() throws InterruptedException;
}
