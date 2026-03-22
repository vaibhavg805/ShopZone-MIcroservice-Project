package com.vaibhav.product_service.repository;

import com.vaibhav.product_service.entity.Brand;
import com.vaibhav.product_service.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand,Long> {

    Optional<Brand> findByName(String name);
    List<Brand> findByIsActiveTrue();

    @Query("select b from Brand b where b.id = :id")
    Brand findByBrandId(Long id);
}
