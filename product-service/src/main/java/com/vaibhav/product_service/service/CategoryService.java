package com.vaibhav.product_service.service;

import com.vaibhav.product_service.dto.CategoryRequest;
import com.vaibhav.product_service.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    List<CategoryResponse> getAll();

    List<CategoryResponse> getActive();

    List<CategoryResponse> getParents();

    List<CategoryResponse> getSubcategories(Long parentId);

    CategoryResponse getById(Long id);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);
}
