package com.chethani.personalization.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chethani.personalization.config.SecurityRoles;
import com.chethani.personalization.dto.ApiResponse;
import com.chethani.personalization.dto.ProductMetadataRequest;
import com.chethani.personalization.service.ProductMetadataService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/product-metadata")
@RequiredArgsConstructor
public class ProductMetadataController {

    private final ProductMetadataService productMetadataService;

    @PostMapping
    @PreAuthorize("hasAuthority('"+ SecurityRoles.WRITE_METADATA +"')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse addProductMetadata(@Valid @RequestBody ProductMetadataRequest request) {
        productMetadataService.addProductMetadata(request);
        return new ApiResponse("Product metadata added successfully");
    }
    
}