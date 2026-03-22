package com.vaibhav.product_service.serviceImpl;

import com.vaibhav.product_service.dto.BrandRequestDto;
import com.vaibhav.product_service.dto.BrandResponseDto;
import com.vaibhav.product_service.entity.Brand;
import com.vaibhav.product_service.exception.BrandExistException;
import com.vaibhav.product_service.exception.ResourceNotFoundException;
import com.vaibhav.product_service.repository.BrandRepository;
import com.vaibhav.product_service.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandsServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    @Override
    public BrandResponseDto createBrand(BrandRequestDto dto) {
        Optional<Brand> brand = brandRepository.findByName(dto.getName());
        if (brand.isPresent()){
            throw new BrandExistException("Brand with name '" + dto.getName() + "' already exists");
        }

        Brand createBrand = new Brand();
        createBrand.setName(dto.getName());
        createBrand.setDescription(dto.getDescription());
        createBrand.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        createBrand.setLogo_url(dto.getLogoUrl());

        Brand res = brandRepository.save(createBrand);
        return BrandResponseDto.builder()
                .id(res.getId())
                .description(res.getDescription())
                .isActive(res.getIsActive())
                .name(res.getName())
                .logoUrl(res.getLogo_url())
                .build();
    }

    @Override
    public BrandResponseDto updateBrand(Long id, BrandRequestDto dto) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand with ID " + id + " not found"));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setLogo_url(dto.getLogoUrl());
        existing.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : existing.getIsActive());

        try {
            Brand saved = brandRepository.save(existing);
            return mapToDto(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new BrandExistException("Brand with name '" + dto.getName() + "' already exists");
        }

    }

    @Override
    public void deleteBrand(Long id) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand with ID " + id + " not found"));
        brandRepository.delete(existing);
    }

    @Override
    public List<BrandResponseDto> getAllBrands() {
         return brandRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BrandResponseDto> getActiveBrands() {
        return brandRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BrandResponseDto getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand with ID " + id + " not found"));
        return mapToDto(brand);
    }

    private BrandResponseDto mapToDto(Brand brand) {
        return BrandResponseDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .logoUrl(brand.getLogo_url())
                .isActive(brand.getIsActive())
                .build();
    }
}
