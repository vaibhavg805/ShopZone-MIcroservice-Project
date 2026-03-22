package com.vaibhav.product_service.controller;

import com.vaibhav.product_service.dto.CategoryRequest;
import com.vaibhav.product_service.dto.CategoryResponse;
import com.vaibhav.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse create(@RequestBody CategoryRequest request) {
        return categoryService.create(request);
    }

    @GetMapping
    public List<CategoryResponse> getAll(){
        return  categoryService.getAll();
    }

    @GetMapping("/active")
    public List<CategoryResponse> getIsActive(){
        return categoryService.getActive();
    }

    @GetMapping("/parent")
    public List<CategoryResponse> getParentIsNull(){
        return categoryService.getParents();
    }

    @GetMapping("/sub-category/{parentId}")
    public List<CategoryResponse> getParentIsNull(@PathVariable Long parentId){
        return categoryService.getSubcategories(parentId);
    }

    @GetMapping("/getByCategoryId")
    public CategoryResponse getByCategoryId(@RequestParam("Id") Long Id){
        return categoryService.getById(Id);
    }

    @PutMapping("/update/{Id}")
    public CategoryResponse updateAPI(@PathVariable Long Id, @RequestBody CategoryRequest categoryRequest){
        return categoryService.update(Id,categoryRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> updateAPI(@PathVariable Long id){
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
