package com.vaibhav.product_service.controller;
import com.vaibhav.product_service.dto.ProductRequestDto;
import com.vaibhav.product_service.dto.ProductResponseDto;
import com.vaibhav.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping("/test")
    public ResponseEntity<String> testApi(@RequestBody String data) {
        logger.info("Test API Called, Data received is  {}",data);
        return ResponseEntity.ok("Success");
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto dto) {
        ProductResponseDto created = productService.createProduct(dto);
        return ResponseEntity.ok(created);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    //  Update Product (Admin/Seller)
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDto dto) {
        ProductResponseDto updated = productService.updateProduct(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Delete (soft)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Search Products
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> searchProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // error I got - Because Spring Data JPA sorting works using entity metadata, not SQL directly.
        // so we must pass the actual field name like "createdAt" here from your entity class
        Pageable pageable =
                PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductResponseDto> results = productService.searchProducts(
                categoryId, brandId, minPrice, maxPrice, pageable
        );
        return ResponseEntity.ok(results);
    }

    // Get Products By Category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponseDto> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    // Get Products By Brand
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByBrand(@PathVariable Long brandId) {
        List<ProductResponseDto> products = productService.getProductsByBrand(brandId);
        return ResponseEntity.ok(products);
    }

    // Get Featured Products (top-rated)
    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponseDto>> getFeaturedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductResponseDto> products = productService.getFeaturedProducts(limit);
        return ResponseEntity.ok(products);
    }

    // Get Trending Products (most recently added)
    @GetMapping("/trending")
    public ResponseEntity<List<ProductResponseDto>> getTrendingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductResponseDto> products = productService.getTrendingProducts(limit);
        return ResponseEntity.ok(products);
    }

    // Get all active products
    @GetMapping("/active")
    public ResponseEntity<List<ProductResponseDto>> getActiveProducts() {
        List<ProductResponseDto> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    // Get all products (admin view)
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}