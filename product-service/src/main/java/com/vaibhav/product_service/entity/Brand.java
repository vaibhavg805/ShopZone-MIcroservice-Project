package com.vaibhav.product_service.entity;

import com.vaibhav.product_service.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brands")
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String logo_url;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

}
