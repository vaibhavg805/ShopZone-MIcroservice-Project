package com.vaibhav.product_service.service;

import com.vaibhav.product_service.dto.ProductImageDto;
import com.vaibhav.product_service.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductImageService {

    ProductImage addImage(Long productId, MultipartFile file);

    ProductImage getImages(Long imageId);

    List<ProductImageDto> getImagesByProduct(Long productId);

    ProductImage getPrimaryImage(Long productId);

    void setPrimaryImage(Long productId, Long imageId);

    void deleteImage(Long imageId);

    void reorderImages(Long productId, Map<Long, Integer> imageOrderMap);
}
