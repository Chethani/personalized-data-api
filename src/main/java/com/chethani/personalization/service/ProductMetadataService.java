package com.chethani.personalization.service;

import org.springframework.stereotype.Service;

import com.chethani.personalization.dto.ProductMetadataRequest;
import com.chethani.personalization.entity.ProductMetadata;
import com.chethani.personalization.repository.ProductMetadataRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductMetadataService {

    private final ProductMetadataRepository productMetadataRepository;

    public void addProductMetadata(ProductMetadataRequest request) {
        log.info("Saving product metadata for productId={}", request.productId());

        ProductMetadata product = new ProductMetadata();
        product.setProductId(request.productId());
        product.setCategory(request.category());
        product.setBrand(request.brand());
        
        productMetadataRepository.save(product);
    }

}
