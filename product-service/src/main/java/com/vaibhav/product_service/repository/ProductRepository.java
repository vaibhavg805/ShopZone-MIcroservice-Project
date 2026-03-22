package com.vaibhav.product_service.repository;

import com.vaibhav.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrue();
    boolean existsBySku(String sku);

    Optional<Product> findByName(String name);

    boolean existsByName(String name);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByBrandId(Long brandId, Pageable pageable);

    Page<Product> findByBasePriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);

    Page<Product> findAll(Pageable pageable);
}
