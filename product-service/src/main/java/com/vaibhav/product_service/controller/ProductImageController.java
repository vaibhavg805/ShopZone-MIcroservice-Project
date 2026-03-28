package com.vaibhav.product_service.controller;

import com.vaibhav.product_service.dto.ProductImageDto;
import com.vaibhav.product_service.entity.ProductImage;
import com.vaibhav.product_service.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/products/product-image")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService imageService;

    @PostMapping("/products/{id}/images")
    public void uploadImage(@PathVariable Long id, @RequestParam MultipartFile file) {
        imageService.addImage(id, file);
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> viewImage(@PathVariable Long imageId){
        ProductImage image = imageService.getImages(imageId);
        try {
            Path path = Paths.get(image.getImageUrl());
            Resource resource = new UrlResource(path.toUri());
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                // fallback to JPEG (or PNG depending on your app)
                contentType = MediaType.IMAGE_JPEG_VALUE;
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch(IOException e){
            throw  new RuntimeException("Failed to Retrieve Images...");
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImageDto>> getImagesByProduct(@PathVariable Long productId){
        List<ProductImageDto> images = imageService.getImagesByProduct(productId);
        return ResponseEntity.ok(images);
    }
}
