package com.vaibhav.product_service.controller;

import com.vaibhav.product_service.dto.BrandRequestDto;
import com.vaibhav.product_service.dto.BrandResponseDto;
import com.vaibhav.product_service.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    // Admin
    @PostMapping
    public ResponseEntity<BrandResponseDto> createBrand(@RequestBody BrandRequestDto dto) {
        BrandResponseDto created = brandService.createBrand(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponseDto> updateBrand(@PathVariable Long id, @RequestBody BrandRequestDto dto) {
        BrandResponseDto updated = brandService.updateBrand(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }

    // Public
    @GetMapping
    public ResponseEntity<List<BrandResponseDto>> getAllBrands() {
        List<BrandResponseDto> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BrandResponseDto>> getActiveBrands() {
        List<BrandResponseDto> brands = brandService.getActiveBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponseDto> getBrandById(@PathVariable Long id) {
        BrandResponseDto brand = brandService.getBrandById(id);
        return ResponseEntity.ok(brand);
    }
}
