package com.vaibhav.product_service.entity;


import com.vaibhav.product_service.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.valueOf(0);

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String specifications;

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column
    @Builder.Default
    private Integer reviewCount = 0;

    // Important error i got if we set data in service layer using builder
    // then we should add @Builder.Default  on fields where we setting deafult value
    // otherwise it will insert null and vice versa means if using POJO
    // then no need tp add @Builder.Default lombok
    // Calculated field
    @Transient
    public BigDecimal getFinalPrice() {
        return basePrice.subtract(basePrice.multiply(discountPercentage).divide(BigDecimal.valueOf(100)));
    }
}