package com.vaibhav.product_service.serviceImpl;

import com.vaibhav.product_service.dto.CategoryRequest;
import com.vaibhav.product_service.dto.CategoryResponse;
import com.vaibhav.product_service.entity.Category;
import com.vaibhav.product_service.exception.CategoryExistException;
import com.vaibhav.product_service.exception.CategoryIdNotExist;
import com.vaibhav.product_service.repository.CategoryRepository;
import com.vaibhav.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        boolean checkInDbIfExist = categoryRepository.existsByName(request.name());
        if (checkInDbIfExist){
            throw new CategoryExistException("Category Name already exist in records...");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        category.setImageUrl(request.imageUrl());
        category.setIsActive(request.isActive());

        if(request.parentId() != null){
            Category parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));

            parent.addChild(category);   // sets both sides
        }
        Category saved = categoryRepository.save(category);
        List<CategoryResponse> categoryResponseList = saved.getChildren()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return CategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .isActive(saved.getIsActive())
                .imageUrl(saved.getImageUrl())
                .parentId(saved.getParent() != null ? saved.getParent().getId() : null)
                .children(categoryResponseList)
                .build();

    }
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParent() != null
                        ? category.getParent().getId()
                        : null)
                .children(
                        category.getChildren() == null
                                ? List.of()
                                : category.getChildren()
                                .stream()
                                .map(this::mapToResponse) // recursion
                                .toList()
                )
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        List<Category> category = categoryRepository.findAllParentWithChildren();
            return category.stream()
                    .map(this::mapToResponse)
                    .toList();
    }

    @Override
    public List<CategoryResponse> getActive() {
       List<Category> list = categoryRepository.findByIsActiveTrue();
       return list.stream().map(this::mapToResponse)
               .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getParents() {
        List<Category> list = categoryRepository.findByParentIsNull();
        return list.stream().map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubcategories(Long parentId) {
        List<Category> list = categoryRepository.findByParentId(parentId);
        return list.stream().map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findByCategoryId(id);
        if(category == null){
            throw new CategoryIdNotExist("Category Id Not Exist...");
        }
        List<CategoryResponse> categoryResponse = category.getChildren()
                .stream()
                .map(this::mapToResponse)
                .toList();
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParent() != null
                        ? category.getParent().getId()
                        : null)
                .children(categoryResponse)
                .build();
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category resCategory = categoryRepository.findById(id).orElseThrow(() ->
                new CategoryIdNotExist("category Id not exist in database"));

        if (Objects.equals(request.parentId(), id)){
            throw new IllegalArgumentException("Category cannot be its own parent");
        }

        if (request.name() != null){
            resCategory.setName(request.name());
        }
        if(request.isActive() != null){
            resCategory.setIsActive(request.isActive());
        }
        if(request.description() != null){
            resCategory.setDescription(request.description());
        }
        if(request.imageUrl() != null){
            resCategory.setImageUrl(request.imageUrl());
        }

        Category oldParent = resCategory.getParent();
        if(request.parentId() != null){
            Category newParent = categoryRepository.findById(request.parentId()).orElseThrow(
                    () -> new CategoryIdNotExist("Parent Id Not Exist"));
            if(oldParent != null){
                oldParent.removeChild(resCategory);
            }
            newParent.addChild(resCategory);
        }else{
            if(oldParent != null){
                oldParent.removeChild(resCategory);
            }
        }

        return mapToResponse(resCategory);
    }

    // prevent deletion if it has children to avoid accidental data loss.
    // first check the children collection, and only delete leaf categories.
    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryIdNotExist("Category not found"));

        // block deletion if children exist
        if (!category.getChildren().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete category that has subcategories. Delete children first."
            );
        }
        categoryRepository.delete(category);
    }
}
