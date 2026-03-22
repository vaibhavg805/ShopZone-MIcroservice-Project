package com.vaibhav.product_service.serviceImpl;

import com.vaibhav.product_service.config.ExternalConfig;
import com.vaibhav.product_service.dto.ProductImageDto;
import com.vaibhav.product_service.entity.Product;
import com.vaibhav.product_service.entity.ProductImage;
import com.vaibhav.product_service.exception.ProductNotFoundException;
import com.vaibhav.product_service.repository.ProductImageRepository;
import com.vaibhav.product_service.repository.ProductRepository;
import com.vaibhav.product_service.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ExternalConfig externalConfig;

    @Override
    public ProductImage addImage(Long productId, MultipartFile file) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product ID Not Exist..."));

            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }
            String uploadDir = Paths.get(externalConfig.getUploadDir(), "products", productId.toString()).toString();
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            // This only creates a Java object in memory.
            // No Path created yet
            Path filePath = Paths.get(uploadDir, fileName);
            // Now after below line
            //creates the file
            // writes bytes
            // saves it to disk
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setImageUrl(filePath.toString());

            return productImageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }

    }

    @Override
    public ProductImage getImages(Long imageId) {
        return productImageRepository.findById(imageId).orElseThrow(
                () -> new RuntimeException("Image Id Not Exist IN Records..."));
    }

    @Override
    public List<ProductImageDto> getImagesByProduct(Long productId) {
        List<ProductImage> image = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        if (image.isEmpty()){
            throw new RuntimeException("No Image for this ID...");
        }
        List<ProductImageDto> dtos = image.stream()
                .map(img -> new ProductImageDto(
                        img.getId(),
                        "/api/product-image/" + img.getId(), // URL for streaming
                        img.getIsPrimary(),
                        img.getDisplayOrder()
                ))
                .toList();
        return dtos;
    }

    @Override
    public ProductImage getPrimaryImage(Long productId) {
        return null;
    }

    @Override
    public void setPrimaryImage(Long productId, Long imageId) {

    }

    @Override
    public void deleteImage(Long imageId) {

    }

    @Override
    public void reorderImages(Long productId, Map<Long, Integer> imageOrderMap) {

    }
}
