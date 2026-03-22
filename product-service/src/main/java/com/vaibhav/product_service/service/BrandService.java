package com.vaibhav.product_service.service;

import com.vaibhav.product_service.dto.BrandRequestDto;
import com.vaibhav.product_service.dto.BrandResponseDto;

import java.util.List;

public interface BrandService {
    // Admin
    BrandResponseDto createBrand(BrandRequestDto dto);

    BrandResponseDto updateBrand(Long id, BrandRequestDto dto);

    void deleteBrand(Long id);

    // Public
    List<BrandResponseDto> getAllBrands();

    List<BrandResponseDto> getActiveBrands();

    BrandResponseDto getBrandById(Long id);
}
