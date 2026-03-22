package com.vaibhav.product_service.repository;

import com.vaibhav.product_service.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(attributePaths = "children")
    List<Category> findByIsActiveTrue();

    @EntityGraph(attributePaths = "children")
    List<Category> findByParentIsNull();

    List<Category> findByParentId(Long parentId);

    boolean existsByName(String name);

    @EntityGraph(attributePaths = "children")
    @Query("select c from Category c where c.parent is null")
    List<Category> findAllParentWithChildren();

    @EntityGraph(attributePaths = "children")
    @Query("select c from Category c where c.id = :id")
    Category findByCategoryId(@Param("id") Long id);
}
